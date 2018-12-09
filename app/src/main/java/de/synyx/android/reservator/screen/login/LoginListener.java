package de.synyx.android.reservator.screen.login;

import de.synyx.android.reservator.domain.CalendarMode;


/**
 * @author  Julia Dasch - dasch@synyx.de
 */
public interface LoginListener {

    void onErrorCloseButtonClick();


    void onCalenderModeClick(CalendarMode calendarMode);
}
