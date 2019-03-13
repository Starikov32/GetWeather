package com.starikov.getweather.main.current_weather;

import android.location.Location;

public interface CurrentWeatherContract {

    interface View {
        //todo Определяем методы по установке и получений значений в любых view
    }

    interface Presenter {
        //todo Определяем методы которые будут вызываться в ответ на действия пользователя или возникновений событий во view
    }

    interface Model {
        //todo Определяем методы по установке и получений значений для модели данных из/в различные источники сеть, диск, база и т.д
    }
}
