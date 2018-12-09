package de.synyx.android.reservator.screen.main.lobby;

import android.support.annotation.NonNull;

import de.synyx.android.reservator.business.calendar.RoomCalendarModel;
import de.synyx.android.reservator.business.calendar.RoomCalendarRepository;
import de.synyx.android.reservator.business.event.EventModel;
import de.synyx.android.reservator.business.event.EventRepository;
import de.synyx.android.reservator.config.Registry;
import de.synyx.android.reservator.domain.MeetingRoom;
import de.synyx.android.reservator.domain.Reservation;

import io.reactivex.Observable;
import io.reactivex.Single;

import java.util.ArrayList;
import java.util.List;


/**
 * @author  Max Dobler - dobler@synyx.de
 */
public class LoadVisibleRoomsUseCase {

    private final RoomCalendarRepository roomCalendarRepository;
    private final EventRepository eventRepository;

    public LoadVisibleRoomsUseCase() {

        roomCalendarRepository = Registry.get(RoomCalendarRepository.class);
        eventRepository = Registry.get(EventRepository.class);
    }

    public Single<List<MeetingRoom>> execute() {

        return
            roomCalendarRepository.loadVisibleRooms() //
            .map(this::toMeetingRoom) //
            .flatMapSingle(this::addReservations) //
            .collect(ArrayList::new, List::add);
    }


    @NonNull
    private MeetingRoom toMeetingRoom(RoomCalendarModel roomCalendar) {

        return new MeetingRoom(roomCalendar.getCalendarId(), roomCalendar.getName());
    }


    private Single<MeetingRoom> addReservations(MeetingRoom meetingRoom) {

        return
            loadEventsFor(meetingRoom) //
            .map(this::toReservation) //
            .collectInto(meetingRoom, MeetingRoom::addReservation);
    }


    @NonNull
    private Reservation toReservation(EventModel event) {

        return new Reservation(event.getId(), event.getName(), event.getBegin(), event.getEnd());
    }


    private Observable<EventModel> loadEventsFor(MeetingRoom meetingRoom) {

        return eventRepository.loadAllEventsForRoom(meetingRoom.getCalendarId());
    }
}
