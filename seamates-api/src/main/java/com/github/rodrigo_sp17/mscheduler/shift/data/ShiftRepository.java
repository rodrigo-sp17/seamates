package com.github.rodrigo_sp17.mscheduler.shift.data;

import com.github.rodrigo_sp17.mscheduler.shift.data.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    @Query("select s from Shift s where s.owner.userInfo.username = :username")
    List<Shift> getAllShiftsByUsername(String username);

    @Query("select s from Shift s where s.owner.userInfo.username = :username " +
            "and s.shiftId = :shiftId")
    Shift getShiftByIdAndUsername(Long shiftId, String username);
}
