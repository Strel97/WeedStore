package com.hellstudios.weedstore.api.store;

import com.hellstudios.weedstore.api.APIException;
import com.hellstudios.weedstore.core.persistence.DAOException;
import com.hellstudios.weedstore.core.persistence.store.StoreDAO;
import com.hellstudios.weedstore.core.persistence.store.StoreEntity;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author strel
 */
public class StoreAPIImpl implements StoreAPI {

    /**
     * {@link Logger} instance.
     */
    private static final Logger log = Logger.getLogger(StoreAPIImpl.class.getName());

    /**
     * {@link StoreDAO} instance.
     */
    private StoreDAO storeDAO;


    @Override
    public void init() {
        storeDAO = new StoreDAO();
    }

    @Override
    public void deInit() {

    }

    @Override
    public void createStore(StoreBean store) throws APIException {
        if (store == null) {
            throw new IllegalArgumentException("Can't create store, bean is null.");
        }
        if (store.getOwner() == null) {
            throw new IllegalArgumentException("Can't create store, owner account can't be null.");
        }

        try {
            // Generating id as UUID
            store.setId(UUID.randomUUID().toString());

            storeDAO.openCurrentSessionWithTransaction();
            storeDAO.create(store.toEntity());
            storeDAO.closeCurrentSessionWithTransaction();

            log.debug(String.format("Created new store %s with owner %s.", store, store.getOwner()));
        } catch (DAOException ex) {
            String msg = "Can't save new store in DB";
            log.error(msg, ex);
            throw new APIException(msg, ex);
        }
    }

    @Override
    public StoreBean getStoreById(String id) throws APIException {
        try {
            storeDAO.openCurrentSessionWithTransaction();
            StoreEntity entity = storeDAO.findById(id);
            storeDAO.closeCurrentSessionWithTransaction();

            return new StoreBean(entity);
        } catch (DAOException ex) {
            String msg = String.format("Can't get store with id [%s]", id);
            log.error(msg, ex);
            throw new APIException(msg, ex);
        }
    }

    @Override
    public List<StoreBean> getAllStores() throws APIException {
        List<StoreBean> storeBeans = new ArrayList<StoreBean>();

        storeDAO.openCurrentSessionWithTransaction();
        List<StoreEntity> entities = storeDAO.findAll();
        storeDAO.closeCurrentSessionWithTransaction();

        for (StoreEntity entity : entities) {
            storeBeans.add(new StoreBean(entity));
        }

        return storeBeans;
    }

    @Override
    public void updateStore(StoreBean store) throws APIException {
        if (store == null) {
            throw new IllegalArgumentException("Can't update store, bean is null.");
        }
        if (store.getOwner() == null) {
            throw new IllegalArgumentException("Can't update store with empty owner account.");
        }

        try {
            storeDAO.openCurrentSessionWithTransaction();
            storeDAO.update(store.toEntity());
            storeDAO.closeCurrentSessionWithTransaction();

            log.debug(String.format("Updated store %s for user %s.", store, store.getOwner()));
        } catch (DAOException ex) {
            String msg = "Can't update store in DB";
            log.error(msg, ex);
            throw new APIException(msg, ex);
        }
    }
}
