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
import java.util.List;
import java.util.Set;
import java.util.*; 
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    List<TimeRange> mandatoryAttendeeAvailability = new ArrayList<>();
    List<TimeRange> allAttendeeAvailability = new ArrayList<>();

    //if the meeting is scheduled for longer than a day, it cannot be scheduled
    if (request.getDuration() > 24*60) {
      return allAttendeeAvailability;
    } 

    //if no attendees or no meetings, any time of day works
    else if ((request.getAttendees().size() == 0 && request.getOptionalAttendees().size() == 0) || events.size() == 0) {
      allAttendeeAvailability.add(TimeRange.WHOLE_DAY);
      return allAttendeeAvailability;
    } 

    //otherwise, find all possible meeting times   
    else {

      //first find the availability for mandatory attendees only 
      List<TimeRange> mandatoryAttendeeMeetingTimes = 
          findAndSortConflictingMeetings(events, request, false);
      
      fixNestedOrOverlappingMeetings(mandatoryAttendeeMeetingTimes);
      mandatoryAttendeeAvailability = findAvailabilityBetweenMeetings(mandatoryAttendeeMeetingTimes, request);

      //if optional attendees, find availability for all attendees and compare to availability for mandatory attendees 
      if (request.getOptionalAttendees().size() > 0) {
        List<TimeRange> allAttendeeMeetingTimes = 
            findAndSortConflictingMeetings(events, request, true);

        fixNestedOrOverlappingMeetings(allAttendeeMeetingTimes);
        allAttendeeAvailability = findAvailabilityBetweenMeetings(allAttendeeMeetingTimes, request);

        //compare mandatory attendees' availability with optional attendees' availability
        if (allAttendeeAvailability.size() > 0 || request.getAttendees().size() == 0) {
          return allAttendeeAvailability;
        }
      }
    }
    return mandatoryAttendeeAvailability;
  }

  //only keep meetings that have overlapping attendees, and sort by start time 
  public List<TimeRange> findAndSortConflictingMeetings(Collection<Event> events, MeetingRequest request, Boolean allAttendees) {
    List<TimeRange> conflictingMeetings = new ArrayList<>();
    
    for (Iterator iterator = events.iterator(); iterator.hasNext();) {
      List<String> newMeetingAttendees = new ArrayList<>();
      if (allAttendees) {
        newMeetingAttendees.addAll(0, request.getOptionalAttendees());
      }
      newMeetingAttendees.addAll(0, request.getAttendees());

      Event event = (Event) iterator.next();
      List<String> existingMeetingAttendees = new ArrayList<>(event.getAttendees());
      newMeetingAttendees.retainAll(existingMeetingAttendees); 
      
      if (newMeetingAttendees.size() != 0) {
        conflictingMeetings.add(event.getWhen());
      }
    }
    
    Collections.sort(conflictingMeetings, Comparator.comparingInt(TimeRange::start)); 
    return conflictingMeetings;
  }

  //remove nested and merge overlapping time ranges
  public void fixNestedOrOverlappingMeetings(List<TimeRange> meetingTimes) {
    int j = 0;
    while (j < meetingTimes.size()-1) {
      TimeRange currentMeetingTime = meetingTimes.get(j);
      TimeRange nextMeetingTime = meetingTimes.get(j+1);
      
      if (currentMeetingTime.contains(nextMeetingTime)) {
        meetingTimes.remove(j+1);
        continue;
      } 
      else if (currentMeetingTime.overlaps(nextMeetingTime)) {
        TimeRange totalMeetingTime = TimeRange.fromStartEnd(currentMeetingTime.start(), nextMeetingTime.end(), false);
        meetingTimes.set(j, totalMeetingTime);
        meetingTimes.remove(j+1);
        continue;
      } 
      else {
        j++;
      }
    }
  }

  //find the time ranges outside of the meetings 
  public List<TimeRange> findAvailabilityBetweenMeetings(List<TimeRange> meetingTimes, MeetingRequest request) {
    List<TimeRange> availability = new ArrayList<>();

    if (meetingTimes.size() == 0) {
      availability.add(TimeRange.WHOLE_DAY);
    }
    else {
      //time before first meeting
      TimeRange firstAvailableTime = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, meetingTimes.get(0).start(), false);
      if (firstAvailableTime.duration() >= request.getDuration()) {
        availability.add(firstAvailableTime);
      }

      //times between meetings
      if (meetingTimes.size() > 1) {
        for (int k = 1; k < meetingTimes.size(); k++) {
          TimeRange nextAvailableTime = TimeRange.fromStartEnd(meetingTimes.get(k-1).end(), meetingTimes.get(k).start(), false);
          if (nextAvailableTime.duration() >= request.getDuration()) {
            availability.add(nextAvailableTime);
          }
        }
      }
        
      //time after last meeting
      TimeRange lastAvailableTime = TimeRange.fromStartEnd(meetingTimes.get(meetingTimes.size()-1).end(), TimeRange.END_OF_DAY, true);
      if (lastAvailableTime.duration() >= request.getDuration()) {
        availability.add(lastAvailableTime);
      }
    }
    return availability;
  }
}