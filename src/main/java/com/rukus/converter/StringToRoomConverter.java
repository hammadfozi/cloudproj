package com.rukus.converter;

import com.rukus.model.Room;
import com.rukus.service.RoomService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToRoomConverter implements Converter<String, Room> {

    private final RoomService roomService;

    @Autowired
    public StringToRoomConverter(RoomService roomService) {
        this.roomService = roomService;
    }

    @NotNull
    @Override
    public Room convert(@NotNull String element) {
        return roomService.findById(Integer.parseInt(element));
    }

}