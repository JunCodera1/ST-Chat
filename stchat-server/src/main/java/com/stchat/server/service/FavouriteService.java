package com.stchat.server.service;

import com.stchat.server.dao.FavouriteDAO;
import com.stchat.server.model.Favourite;

import java.util.List;
import java.util.Optional;

public class FavouriteService {
    private final FavouriteDAO favouriteDAO = new FavouriteDAO();

    public boolean addFavourite(int userId, int favoriteUserId) {
        return favouriteDAO.addFavourite(userId, favoriteUserId);
    }

    public boolean removeFavourite(int userId, int favoriteUserId) {
        return favouriteDAO.removeFavourite(userId, favoriteUserId);
    }

    public List<Favourite> getFavoritesByUserId(int userId) {
        return favouriteDAO.getFavoritesByUserId(userId);
    }

    public Optional<Favourite> getFavorite(int userId, int favoriteUserId) {
        return Optional.ofNullable(favouriteDAO.getFavorite(userId, favoriteUserId));
    }

    public boolean isFavoriteExists(int userId, int favoriteUserId) {
        return favouriteDAO.isFavoriteExists(userId, favoriteUserId);
    }
}
