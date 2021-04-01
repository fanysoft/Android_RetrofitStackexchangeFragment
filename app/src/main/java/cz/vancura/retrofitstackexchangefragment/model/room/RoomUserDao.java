package cz.vancura.retrofitstackexchangefragment.model.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/*
Room dB - Data Access Object - queries on dB data
 */
@Dao
public interface RoomUserDao {

    // read all from dB
    @Query("SELECT * FROM roomuserpojo")
    List<RoomUserPOJO> getAllUsers();

    // read some by id from dB
    @Query("SELECT * FROM roomuserpojo WHERE my_user_id IN (:userIds)")
    List<RoomUserPOJO> getUserByIds(int[] userIds);

    // insert into dB
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(RoomUserPOJO... users);

    // delete from dB
    @Delete
    void delete(RoomUserPOJO user);

    // delete from dB - all
    @Query("DELETE FROM roomuserpojo")
    public void deleteAllData();

}
