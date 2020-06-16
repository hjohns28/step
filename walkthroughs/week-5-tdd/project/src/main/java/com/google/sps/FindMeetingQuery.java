// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Set;
import java.util.*; 
import java.util.Comparator;
import java.util.Collections;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    ArrayList<TimeRange> mandatoryAttendeeAvailability = new ArrayList<>();
    ArrayList<TimeRange> allAttendeeAvailability = new ArrayList<>();

    //if no attendees, any time of day works 
    if (request.getAttendees().size() == 0 && request.getOptionalAttendees().size() == 0) {
      allAttendeeAvailability.add(TimeRange.WHOLE_DAY);
      return allAttendeeAvailability;
    } 

    //if the meeting is scheduled for longer than a day, it cannot be scheduled
    else if (request.getDuration() > 24*60) {
      return allAttendeeAvailability;
    } 

    //if there are no meetings, any time of day works 
    else if (events.size() == 0) {
      allAttendeeAvailability.add(TimeRange.WHOLE_DAY);
      return allAttendeeAvailability;
    }

    //otherwise, find all possible meeting times   
    else {

      //first find the availability for mandatory attendees only 
      ArrayList<Event> mandatoryAttendeeMeetings = new ArrayList<>(events);
    
      //remove existing meetings that have no overlapping attendees (not a conflict)
      int i = 0;
      while (i < mandatoryAttendeeMeetings.size()) {
        ArrayList<String> newMeetingMandatoryAttendees = new ArrayList<>(request.getAttendees());
        ArrayList<String> existingMeetingAttendees = new ArrayList<>(mandatoryAttendeeMeetings.get(i).getAttendees());
        newMeetingMandatoryAttendees.retainAll(existingMeetingAttendees); 
        if (newMeetingMandatoryAttendees.size() == 0) {
          mandatoryAttendeeMeetings.remove(i);
          continue;
        } else {
          i++;
        }
      }

      //copy only the time ranges from the meetings, and sort 
      ArrayList<TimeRange> mandatoryAttendeeMeetingTimes = new ArrayList<>();
      for (int b = 0; b < mandatoryAttendeeMeetings.size(); b++) {
        mandatoryAttendeeMeetingTimes.add(mandatoryAttendeeMeetings.get(b).getWhen());
      }
      Collections.sort(mandatoryAttendeeMeetingTimes, Comparator.comparingInt(TimeRange::start)); 

      //check for nested or overlapping meetings
      int j = 0;
      while (j < mandatoryAttendeeMeetingTimes.size()-1) {
        TimeRange currentMeetingTime = mandatoryAttendeeMeetingTimes.get(j);
        TimeRange nextMeetingTime = mandatoryAttendeeMeetingTimes.get(j+1);
        
        if (currentMeetingTime.contains(nextMeetingTime)) {
          mandatoryAttendeeMeetingTimes.remove(j+1);
          continue;
        } 
        else if (currentMeetingTime.overlaps(nextMeetingTime)) {
          TimeRange totalMeetingTime = TimeRange.fromStartEnd(currentMeetingTime.start(), nextMeetingTime.end(), false);
          mandatoryAttendeeMeetingTimes.set(j, totalMeetingTime);
          mandatoryAttendeeMeetingTimes.remove(j+1);
          continue;
        } 
        else {
          j++;
        }
      }

      //if no conflicting meetings (all were removed), any time of day works  
      if (mandatoryAttendeeMeetingTimes.size() == 0) {
        mandatoryAttendeeAvailability.add(TimeRange.WHOLE_DAY);
      }

      //otherwise, find available time ranges outside of meetings 
      else {
        
        //time before first meeting
        TimeRange firstAvailableTime = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, mandatoryAttendeeMeetingTimes.get(0).start(), false);
        if (firstAvailableTime.duration() > 0) {
          mandatoryAttendeeAvailability.add(firstAvailableTime);
        }

        //times between meetings
        if (mandatoryAttendeeMeetingTimes.size() > 1) {
          for (int k = 1; k < mandatoryAttendeeMeetingTimes.size(); k++) {
            TimeRange nextAvailableTime = TimeRange.fromStartEnd(mandatoryAttendeeMeetingTimes.get(k-1).end(), mandatoryAttendeeMeetingTimes.get(k).start(), false);
            if (nextAvailableTime.duration() > 0) {
              mandatoryAttendeeAvailability.add(nextAvailableTime);
            }
          }
        }
        
        //time after last meeting
        TimeRange lastAvailableTime = TimeRange.fromStartEnd(mandatoryAttendeeMeetingTimes.get(mandatoryAttendeeMeetingTimes.size()-1).end(), TimeRange.END_OF_DAY, true);
        if (lastAvailableTime.duration() > 0) {
          mandatoryAttendeeAvailability.add(lastAvailableTime);
        }
      }

      //remove any time ranges that aren't long enough 
      int m = 0;
      while (m < mandatoryAttendeeAvailability.size()) {
        if (mandatoryAttendeeAvailability.get(m).duration() < request.getDuration()) {
          mandatoryAttendeeAvailability.remove(m);
          continue;
        } else {
          m++;
        }
      }

      //if there are optional attendees, find the availability for all attendees, and compare to 
      //the availability for mandatory attendees 
      if (request.getOptionalAttendees().size() > 0) {
        ArrayList<Event> allAttendeeMeetings = new ArrayList<>(events);

        //remove meetings with no overlapping attendees
        i = 0;
        while (i < allAttendeeMeetings.size()) {
          ArrayList<String> newMeetingAttendees = new ArrayList<>();
          newMeetingAttendees.addAll(0, request.getOptionalAttendees());
          newMeetingAttendees.addAll(0, request.getAttendees());
          ArrayList<String> existingMeetingAttendees = new ArrayList<>(allAttendeeMeetings.get(i).getAttendees());
        
          newMeetingAttendees.retainAll(existingMeetingAttendees); 
          if (newMeetingAttendees.size() == 0) {
            allAttendeeMeetings.remove(i);
            continue;
          } else {
            i++;
          }
        }

        //copy time ranges from meetings and sort 
        ArrayList<TimeRange> allAttendeeMeetingTimes = new ArrayList<>();
        for (int c = 0; c < allAttendeeMeetings.size(); c++) {
          allAttendeeMeetingTimes.add(allAttendeeMeetings.get(c).getWhen());
        }
        Collections.sort(allAttendeeMeetingTimes, Comparator.comparingInt(TimeRange::start));

        //check for nested or overlapping time ranges
        j = 0;
        while (j < allAttendeeMeetingTimes.size()-1) {
          TimeRange currentMeetingTime = allAttendeeMeetingTimes.get(j);
          TimeRange nextMeetingTime = allAttendeeMeetingTimes.get(j+1);
        
          if (currentMeetingTime.contains(nextMeetingTime)) {
            allAttendeeMeetingTimes.remove(j+1);
            continue;
          } 
          else if (currentMeetingTime.overlaps(nextMeetingTime)) {
            TimeRange totalMeetingTime = TimeRange.fromStartEnd(currentMeetingTime.start(), nextMeetingTime.end(), false);
            allAttendeeMeetingTimes.set(j, totalMeetingTime);
            allAttendeeMeetingTimes.remove(j+1);
            continue;
          } 
          else {
            j++;
          }
        }

        //if no conflicting meetings (all were removed), any time of day works  
        if (allAttendeeMeetingTimes.size() == 0) {
          allAttendeeAvailability.add(TimeRange.WHOLE_DAY);
        }

        //otherwise, find times outside of meetings 
        else {
        
          //time before first meeting
          TimeRange firstAvailableTime = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, allAttendeeMeetingTimes.get(0).start(), false);
          if (firstAvailableTime.duration() > 0) {
            allAttendeeAvailability.add(firstAvailableTime);
          }

          //times between meetings
          if (allAttendeeMeetingTimes.size() > 1) {
            for (int k = 1; k < allAttendeeMeetingTimes.size(); k++) {
              TimeRange nextAvailableTime = TimeRange.fromStartEnd(allAttendeeMeetingTimes.get(k-1).end(), allAttendeeMeetingTimes.get(k).start(), false);
              if (nextAvailableTime.duration() > 0) {
                allAttendeeAvailability.add(nextAvailableTime);
              }
            }
          }
        
          //time after last meetings
          TimeRange lastAvailableTime = TimeRange.fromStartEnd(allAttendeeMeetingTimes.get(allAttendeeMeetingTimes.size()-1).end(), TimeRange.END_OF_DAY, true);
          if (lastAvailableTime.duration() > 0) {
            allAttendeeAvailability.add(lastAvailableTime);
          }
        }

        //check that time ranges are long enough 
        m = 0;
        while (m < allAttendeeAvailability.size()) {
          if (allAttendeeAvailability.get(m).duration() < request.getDuration()) {
            allAttendeeAvailability.remove(m);
            continue;
          } else {
            m++;
          }
        }

        //compare mandatory attendees' availability with optional attendees' availability
        if (allAttendeeAvailability.size() > 0 || request.getAttendees().size() == 0) {
          return allAttendeeAvailability;
        }
      }
    }
    return mandatoryAttendeeAvailability;
  }
}
