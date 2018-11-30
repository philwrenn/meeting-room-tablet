package de.synyx.android.reservator.screen.settings;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import de.synyx.android.reservator.config.Registry;
import de.synyx.android.reservator.screen.RoomDto;
import de.synyx.android.reservator.screen.main.lobby.LoadRoomsUseCase;
import de.synyx.android.reservator.util.SchedulerFacade;

import io.reactivex.disposables.Disposable;

import java.util.List;


/**
 * @author  Max Dobler - dobler@synyx.de
 */
public class SettingsViewModel extends ViewModel {

    private MutableLiveData<List<RoomDto>> rooms;

    private LoadRoomsUseCase loadRoomsUseCase;
    private SchedulerFacade schedulerFacade;
    private Disposable disposable;

    public SettingsViewModel() {

        loadRoomsUseCase = Registry.get(LoadRoomsUseCase.class);
        schedulerFacade = Registry.get(SchedulerFacade.class);
    }

    public LiveData<List<RoomDto>> getRooms() {

        if (rooms == null) {
            rooms = new MutableLiveData<>();
            loadRooms();
        }

        return rooms;
    }


    private void loadRooms() {

        disposable = loadRoomsUseCase.execute()
                .observeOn(schedulerFacade.io())
                .subscribeOn(schedulerFacade.mainThread())
                .subscribe(rooms::postValue);
    }


    @Override
    protected void onCleared() {

        super.onCleared();
        disposable.dispose();
    }
}