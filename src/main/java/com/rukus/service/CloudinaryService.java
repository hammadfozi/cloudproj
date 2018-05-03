package com.rukus.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rukus.Constant;
import com.rukus.model.Room;
import com.rukus.model.RoomImages;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CloudinaryService {

    public static void deleteRoomImages(final Room room, final RoomImageService roomImagesService) {
        List<String> publicIds = new ArrayList<>();
        for (RoomImages image : room.getImages()) {
            publicIds.add(image.getName());
        }

        Cloudinary cloudinary = new Cloudinary(Constant.CLOUDINARY.URL);
        try {
            cloudinary.api().deleteResources(publicIds, ObjectUtils.emptyMap());
        } catch (Exception e) {
            e.printStackTrace();
        }

        roomImagesService.deleteByRoomId(room.getId());
    }

    public static void saveImage(final Room room, List<MultipartFile> images, final RoomImageService roomImagesService) throws IOException {
        Cloudinary cloudinary = new Cloudinary(Constant.CLOUDINARY.URL);

        for (int i = 0; i < images.size(); i++) {
            String roomName = room.getName();
            roomName = roomName.replaceAll("\\s", "");
            roomName = roomName + "_" + i;

            cloudinary.uploader().upload(images.get(i).getBytes(),
                    ObjectUtils.asMap("public_id", roomName));

            String extension = ".jpg";
            if (images.get(i).getContentType().toLowerCase().contains("png")) {
                extension = ".png";
            }

            RoomImages roomImage = new RoomImages();
            roomImage.setName(roomName);
            roomImage.setUrl(cloudinary.url().generate(roomName) + extension);
            roomImage.setRoom(room);
            roomImagesService.save(roomImage);
        }
    }
}
