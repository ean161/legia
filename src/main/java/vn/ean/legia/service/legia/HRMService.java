package vn.ean.legia.service.legia;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import vn.ean.legia.model.legia.Staff;
import vn.ean.legia.model.legia.StaticMoney;
import vn.ean.legia.model.legia.TimeSheet;
import vn.ean.legia.repository.legia.StaffRepository;
import vn.ean.legia.repository.legia.StaticMoneyRepository;
import vn.ean.legia.repository.legia.TimeSheetRepository;
import vn.ean.legia.util.Lib;

@Service("legiaHRMService")
public class HRMService {
    
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Autowired
    private StaticMoneyRepository staticMoneyRepository;

    public Page<Staff> list(String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            int searchNum;

            try {
                searchNum = Integer.parseInt(search);
            } catch (Exception ex) {
                searchNum = 0;
            }

            return staffRepository.findByNameContainingIgnoreCaseOrSalary(search, searchNum, pageable);
        }

        return staffRepository.findAll(pageable);
    }

    public Staff add(String name, Integer salary) {
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("Tên nhân viên không hợp lệ");
        } else if (staffRepository.existsByName(name)) {
            throw new RuntimeException("Tên nhân viên đã tồn tại");
        } else if (salary == null || salary <= 0) {
            throw new RuntimeException("Lương cơ bản không hợp lệ");
        }

        return staffRepository.save(new Staff(name, salary));
    }

    public Staff edit(Long id, String name, Integer salary) {
        if (id == null || !staffRepository.existsById(id)) {
            throw new RuntimeException("Nhân viên không tồn tại");
        } else if (name == null || name.isEmpty()) {
            throw new RuntimeException("Tên nhân viên không hợp lệ");
        } else if (salary == null || salary <= 0) {
            throw new RuntimeException("Lương không hợp lệ");
        }

        return staffRepository.save(new Staff(id, name, salary));
    }

    public void delete(Long id) {
        if (id == null || !staffRepository.existsById(id)) {
            throw new RuntimeException("Nhân viên không tồn tại");
        }

        staffRepository.deleteById(id);
    }

    public Map<String, Object> statistic(Long id, String month) {
        if (id == null || !staffRepository.existsById(id)) {
            throw new RuntimeException("Nhân viên không tồn tại");
        } else if (month == null || month.isEmpty() || !month.contains("/")) {
            throw new RuntimeException("Tháng thống kê không hợp lệ");
        }

        Map<String, Object> result = new HashMap<>();
        Staff staff = staffRepository.findById(id).orElseThrow();

        Map<String, Long> targetMonth = Lib.getMonthTimestamps(month);
        List<TimeSheet> shifts = timeSheetRepository.findByStaffAndStartGreaterThanAndEndLessThanAndEndGreaterThan(staff, targetMonth.get("start"), targetMonth.get("end"), 0L);
        List<StaticMoney> statics = staticMoneyRepository.findByStaffAndCreatedDateGreaterThanAndCreatedDateLessThan(staff, targetMonth.get("start"), targetMonth.get("end"));

        result.put("month", targetMonth);
        result.put("shifts", shifts);
        result.put("statics", statics);
        return result;
    }

    public StaticMoney createStaticMoney(Long id, String note, String type, Integer amount) {
        if (id == null || !staffRepository.existsById(id)) {
            throw new RuntimeException("Nhân viên không tồn tại");
        } else if (note == null || note.isEmpty()) {
            throw new RuntimeException("Ghi chú không hợp lệ");
        } else if (amount == null || amount == 0) {
            throw new RuntimeException("Số tiền không hợp lệ");
        } else if (type == null || type.isEmpty()) {
            throw new RuntimeException("Cần chọn cộng hoặc trừ tiền trước");
        }

        if (type.equals("SUB")) {
            amount *= -1;
        }

        Staff staff = staffRepository.findById(id).orElseThrow();
        return staticMoneyRepository.save(new StaticMoney(staff, note, amount));
    }

    @Transactional
    public void editStaticMoney(Long id, String note, String type, Integer amount) {
        if (id == null || !staticMoneyRepository.existsById(id)) {
            throw new RuntimeException("Khoản cộng/trừ này không tồn tại");
        } else if (note == null || note.isEmpty()) {
            throw new RuntimeException("Ghi chú không hợp lệ");
        } else if (amount == null || amount == 0) {
            throw new RuntimeException("Số tiền không hợp lệ");
        } else if (type == null || type.isEmpty()) {
            throw new RuntimeException("Cần chọn cộng hoặc trừ tiền trước");
        }

        if (type.equals("SUB")) {
            amount *= -1;
        }

        StaticMoney entity = staticMoneyRepository.findById(id).orElseThrow();
        entity.setNote(note);
        entity.setAmount(amount);
    }

    public TimeSheet startWork(Long id) {
        if (id == null || !staffRepository.existsById(id)) {
            throw new RuntimeException("Nhân viên không tồn tại");
        }

        List<TimeSheet> listSession = timeSheetRepository.findListByStaffAndEnd(staffRepository.findById(id).orElseThrow(), 0);
        if (!listSession.isEmpty()) {
            throw new RuntimeException("Nhân viên này đang trong ca làm");
        }

        return timeSheetRepository.save(new TimeSheet(staffRepository.findById(id).orElseThrow()));
    }

    public TimeSheet endWork(Long id) {
        if (id == null || !staffRepository.existsById(id)) {
            throw new RuntimeException("Nhân viên không tồn tại");
        }

        Long now = Lib.currentTimestamp();
        TimeSheet workSession = timeSheetRepository.findByStaffAndEnd(staffRepository.findById(id).orElseThrow(), 0);
        if (workSession == null || workSession.getEnd() != 0) {
            throw new RuntimeException("Ca này đã kết thúc từ trước");
        }

        Map<String, Integer> detail = sessionDetail(now, workSession);
        timeSheetRepository.updateEndByStaffId(now, (long) detail.get("normal"), (long) detail.get("ot"), workSession.getId());

        workSession.setEnd(now);
        workSession.setWorktime((long) detail.get("normal"));
        workSession.setOvertime((long) detail.get("ot"));

        return workSession;
    }

    public int endAllWork() {
        List<TimeSheet> unEndWorks = timeSheetRepository.findByEnd(0L);
        if (unEndWorks.isEmpty()) {
            throw new RuntimeException("Hiện tại không có nhân viên làm việc để kết ca");
        }

        for (TimeSheet timeSheet : unEndWorks) {
            endWork(timeSheet.getStaff().getId());
        }

        return unEndWorks.size();
    }

    public Map<String, Integer> sessionDetail(Long now, TimeSheet work) {
        HashMap<String, Integer> result = new HashMap<>();
        Long todayOTMock = Lib.getTimestampAt("18:00");

        Long start = work.getStart(), end = now;
        int normalCount = 0, otCount = 0;

        if (start < todayOTMock && end < todayOTMock) {
            normalCount += end - start;
        } else if (start < todayOTMock && end >= todayOTMock) {
            normalCount += todayOTMock - start;
            otCount += end - todayOTMock;
        } else {
            otCount += end - start;
        }

        result.put("normal", normalCount <= 60 ? 0 : normalCount);
        result.put("ot", otCount <= 60 ? 0 : otCount);
        return result;
    }

    public String massAction(List<String> ids, String action) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("Không có nhân viên nào được chọn.");
        }

        int count = 0;
        for (String id : ids) {
            try {
                if (action.equals("START")) {
                    startWork(Long.parseLong(id));
                } else {
                    endWork(Long.parseLong(id));
                }

                count++;
            } catch (Exception e) { }
        }

        return String.format("%d/%d %s ca thành công.", count, ids.size(), action.equals("START") ? "vào" : "kết");
    }
}
