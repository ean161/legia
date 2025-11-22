package vn.ean.legia.service.legia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.ean.legia.model.legia.TimeSheet;
import vn.ean.legia.repository.legia.TimeSheetRepository;
import vn.ean.legia.util.Lib;

import java.util.Map;

@Service("legiaShiftsService")
public class ShiftsService {
    
    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Autowired
    private HRMService hrmService;

    public Page<TimeSheet> list(String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            return timeSheetRepository.findByStaff_NameContainingIgnoreCase(search, pageable);
        }

        return timeSheetRepository.findAll(pageable);
    }

    public TimeSheet edit(Long id, String start, String end) {
        if (id == null || !timeSheetRepository.existsById(id)) {
            throw new RuntimeException("Ca chấm công này không tồn tại");
        }

        TimeSheet shift = timeSheetRepository.findById(id).orElseThrow();

        Long oldStart = shift.getStart();
        Long oldEnd = shift.getEnd();

        Long now = Lib.currentTimestamp();
        Long startTimestamp = Lib.getTodayTimestampByHHMM(start);

        if (startTimestamp > now) {
            throw new RuntimeException("Không thể đặt thời gian vào ca sớm hơn hiện tại");
        }

        try {
            shift.setStart(startTimestamp);

            if (shift.getEnd() != 0) {
                Long endTimestamp = Lib.getTodayTimestampByHHMM(end);

                if (endTimestamp > now) {
                    throw new RuntimeException("Không thể đặt thời gian kết ca sớm hơn hiện tại");
                }

                if (startTimestamp >= endTimestamp) {
                    throw new RuntimeException("Thời gian vào hoặc kết ca không hợp lệ");
                }

                shift.setEnd(endTimestamp);

                Map<String, Integer> detail = hrmService.sessionDetail(endTimestamp, shift);
                shift.setWorktime((long) detail.get("normal"));
                shift.setOvertime((long) detail.get("ot"));
            }

            if (Math.abs(oldStart - shift.getStart()) > 60) {
                shift.setStartEdited(true);
            }

            if (Math.abs(oldEnd - shift.getEnd()) > 60) {
                shift.setEndEdited(true);
            }
        } catch (Exception e) {
            throw new RuntimeException("Giờ vào hoặc kết ca không hợp lệ");
        }

        return timeSheetRepository.save(shift);
    }
}
