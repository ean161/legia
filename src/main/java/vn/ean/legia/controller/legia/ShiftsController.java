package vn.ean.legia.controller.legia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import vn.ean.legia.model.legia.TimeSheet;
import vn.ean.legia.service.legia.ShiftsService;
import vn.ean.legia.util.Response;

import java.util.Map;

@Controller("legiaShiftsController")
@RequestMapping("/legia/shifts")
public class ShiftsController {
    
    @Autowired
    private ShiftsService shiftsService;

    private final Response res = new Response();

    @GetMapping
    public String index() {
        return "pages/legia/shifts";
    }

    @GetMapping("/list")
    @ResponseBody
    public Page<TimeSheet> list(@RequestParam(required = false) String search, Pageable pageable) {
        return shiftsService.list(search, pageable);
    }

    @PostMapping("/edit")
    @ResponseBody
    public Map<String, Object> edit(@RequestParam(required = false) Long id, @RequestParam(required = false) String start, @RequestParam(required = false) String end) {
        try {
            TimeSheet shift = shiftsService.edit(id, start, end);
            return res.create(1, "Sửa chấm công thành công", shift);
        } catch (Exception e) {
            return res.create(0, e.getMessage(), null);
        }
    }
}
