package com.rukus.service;

import com.rukus.model.Room;
import com.rukus.service.rep.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @Transactional changes to model is reflected automatically in database
 */

@Service("roomService")
@Transactional
public class RoomService {

    private final RoomRepository dao;

    @Autowired
    public RoomService(RoomRepository dao) {
        this.dao = dao;
    }

    public Room findById(int id) {
        Optional<Room> tmp = dao.findById(id);
        return tmp.orElse(null);
    }

    public Room findByName(String name) {
        return dao.findByName(name).orElse(null);
    }

    public void saveRoom(@NonNull Room room) {
        dao.save(room);
    }

    public void updateRoom(@NonNull Room room) {
        Optional<Room> entity = dao.findById(room.getId());
        if (entity.isPresent()) {
            room.setId(entity.get().getId());
            dao.save(room);
        }
    }

    public void deleteRoomById(int id) {
        dao.deleteById(id);
    }

    public List<Room> findAllRooms() {
        return dao.findAll();
    }

    public boolean isRoomNameUnique(Integer id, String name) {
        Room room = findByName(name);
        return (room == null || ((id != null) && (room.getId().equals(id))));
    }

    public List<Room> findFreeRooms() {
        return dao.findFreeRooms();
    }
}
