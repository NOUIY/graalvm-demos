/*
 * Copyright (c) 2024, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at https://opensource.org/license/UPL.
 */

package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
class PhotonService {
    private final PhotonPool photonPool;

    PhotonService(PhotonPool photonPool) {
        this.photonPool = photonPool;
    }

    public byte[] processImage(String effectName) {
        Photon photon = photonPool.take();
        try {
            if (!photon.implementsEffect(effectName)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Effect " + effectName + " not found in Photon");
            }
            Photon.PhotonImage image = photon.createImage();
            photon.applyEffect(effectName, image);
            byte[] result = Photon.toByteArray(image);
            image.free();
            return result;
        } finally {
            photonPool.release(photon);
        }
    }
}
