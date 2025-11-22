package vn.ean.legia.controller.legia;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import vn.ean.legia.model.legia.Staff;
import vn.ean.legia.model.legia.StaticMoney;
import vn.ean.legia.model.legia.TimeSheet;
import vn.ean.legia.service.legia.HRMService;
import vn.ean.legia.util.Response;

@Controller("legiaHRMController")
@RequestMapping("/legia/hrm")
public class HRMController {
    
    @Autowired
    private HRMService hrmService;

    private final Response res = new Response();

    @GetMapping
    public String index(Model model) {
        List<String> months = new ArrayList<>();
        
        YearMonth current = YearMonth.now();
        months.add(String.format("%02d/%d", current.getMonthValue(), current.getYear()));
        
        for (int i = -6; i <= 6; i++) {
            YearMonth m = current.plusMonths(i);
            months.add(String.format("%02d/%d", m.getMonthValue(), m.getYear()));
        }

        model.addAttribute("months", months);
        return "pages/legia/hrm";
    }

    @GetMapping("/list")
    @ResponseBody
    public Page<Staff> list(@RequestParam(required = false) String search, Pageable pageable) {
        return hrmService.list(search, pageable);
    }

    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> add(@RequestParam String name, @RequestParam Integer salary) {
        try {
            Staff staff = hrmService.add(name, salary);
            return res.create(1, "Thêm nhân viên thành công", staff);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public Map<String, Object> edit(@RequestParam Long id, @RequestParam String name, @RequestParam Integer salary) {
        try {
            Staff staff = hrmService.edit(id, name, salary);
            return res.create(1, "Sửa nhân viên thành công", staff);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> delete(@RequestParam Long id) {
        try {
            hrmService.delete(id);
            return res.create(1, "Xoá nhân viên thành công", null);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @PostMapping("/statistic")
    @ResponseBody
    public Map<String, Object> statistic(@RequestParam Long id, @RequestParam String month) {
        try {
            Map<String, Object> data = hrmService.statistic(id, month);
            return res.create(1, null, data);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @PostMapping("/create-static-money")
    @ResponseBody
    public Map<String, Object> createStaticMoney(@RequestParam Long id, @RequestParam String note, @RequestParam String type, @RequestParam Integer amount) {
        try {
            StaticMoney sm = hrmService.createStaticMoney(id, note, type, amount);
            return res.create(1, (type.equals("PLUS") ? "Cộng" : "Trừ" )+ " lương thành công", sm);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @PostMapping("/edit-static-money")
    @ResponseBody
    public Map<String, Object> editStaticMoney(@RequestParam Long id, @RequestParam String note, @RequestParam String type, @RequestParam Integer amount) {
        try {
            hrmService.editStaticMoney(id, note, type, amount);
            return res.create(1, "Cập nhật thành công", null);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @PostMapping("/mass-action")
    @ResponseBody
    public Map<String, Object> massAction(@RequestParam(value = "ids[]") List<String> ids,
                                          @RequestParam(value = "action") String action) {
        try {
            return res.create(1, hrmService.massAction(ids, action), null);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @PostMapping("/start-work")
    @ResponseBody
    public Map<String, Object> startWork(@RequestParam Long id) {
        try {
            TimeSheet ts = hrmService.startWork(id);
            return res.create(1, "Vào ca thành công", ts);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @PostMapping("/end-work")
    @ResponseBody
    public Map<String, Object> endWork(@RequestParam Long id) {
        try {
            TimeSheet ts = hrmService.endWork(id);
            return res.create(1, "Kết ca thành công", ts);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }

    @GetMapping("/end-all-work")
    @ResponseBody
    public Map<String, Object> endAllWork() {
        try {
            int closed = hrmService.endAllWork();
            return res.create(1, closed + " ca đã được đóng", closed);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }
}
