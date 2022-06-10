package com.github.rodrigo_sp17.mscheduler.shift;

import com.github.rodrigo_sp17.mscheduler.shift.data.Shift;
import com.github.rodrigo_sp17.mscheduler.shift.data.ShiftRepository;
import com.github.rodrigo_sp17.mscheduler.shift.exception.ShiftNotFoundException;
import com.github.rodrigo_sp17.mscheduler.user.UserService;
import com.github.rodrigo_sp17.mscheduler.user.data.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShiftService {

    @Autowired
    private final ShiftRepository shiftRepository;

    @Autowired
    private final UserService userService;

    public ShiftService(ShiftRepository shiftRepository,
                        UserService userService) {
        this.shiftRepository = shiftRepository;
        this.userService = userService;
    }

    public List<Shift> getShiftsForUser(String username) {
        return shiftRepository.getAllShiftsByUsername(username);
    }

    public Shift getShiftById(Long shiftId, String requester) {
        Shift result = shiftRepository.getShiftByIdAndUsername(shiftId, requester);
        if (result == null) {
            throw new ShiftNotFoundException("Could not find shiftId " + shiftId);
        }
        return result;
    }

    @Transactional
    public List<Shift> addShifts(List<Shift> shifts, String username) {
        AppUser owner = userService.getUserByUsername(username);
        for (Shift s : shifts) {
            s.setOwner(owner);
        }
        List<Shift> inserted = shiftRepository.saveAll(shifts);

        owner.setShifts(shifts);
        userService.saveUser(owner);
        return inserted;
    }

    @Transactional
    public Shift editShift(Shift shift) {
        return shiftRepository.save(shift);
    }

    @Transactional
    public void removeShift(Long shiftId) {
        shiftRepository.deleteById(shiftId);
    }
}
