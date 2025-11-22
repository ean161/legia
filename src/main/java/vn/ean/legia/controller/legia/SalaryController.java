package vn.ean.legia.controller.legia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import vn.ean.legia.model.legia.SalaryInMonth;
import vn.ean.legia.service.legia.SalaryService;

@Controller("legiaSalaryController")
@RequestMapping("/legia/salary")
public class SalaryController {
    
    @Autowired
    private SalaryService salaryService;

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
        return "pages/legia/salary";
    }

    @GetMapping("/list")
    @ResponseBody
    public Page<SalaryInMonth> list(@RequestParam(required = false) String month, @RequestParam(required = false) String search, Pageable pageable) {
        return salaryService.list(month, search, pageable);
    }

    @PostMapping("/export")
    @ResponseBody
    public String export(@RequestParam(required = false) String month) {
        return salaryService.export(month);
    }

    @GetMapping("/download-export")
    public ResponseEntity<InputStreamResource> downloadExcel() throws FileNotFoundException {
        File file = new File("legia_bang_luong.xlsx");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .contentLength(file.length())
            .body(resource);
    }
}
