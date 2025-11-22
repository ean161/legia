package vn.ean.legia.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.ean.legia.model.Transaction;
import vn.ean.legia.model.User;
import vn.ean.legia.repository.TransactionRepository;
import vn.ean.legia.repository.UserRepository;
import vn.ean.legia.util.Api;
import vn.ean.legia.util.Lib;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${acb.account}")
    private String acbAccount;

    @Value("${acb.password}")
    private String acbPassword;

    @Value("${acb.key}")
    private String acbKey;

    @Value("${acb.device}")
    private String acbDevice;

    @Value("${acb.client}")
    private String acbClient;

    public Map<String, Object> bankingScan() {
        String token;

        try {
            Map<String, Object> getToken = Api.post(
                "https://apiapp.acb.com.vn/mb/v2/auth/tokens",
                Map.of(
                    "apikey", acbKey,
                    "Content-Type",  "application/json"
                ),
                Map.of(
                    "username", acbAccount,
                    "password", acbPassword,
                    "deviceId", acbDevice,
                    "clientId", acbClient
                )
            );

            token = (String) getToken.get("accessToken");
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy token ACB");
        }

        try {
            Map<String, Object> getHis = Api.get(
                String.format("https://apiapp.acb.com.vn/mb/legacy/ss/cs/person/transaction-history/list?account=%s&transactionType=ALL&from=%d&to=%d&min=&max=", acbAccount, (Lib.currentTimestamp() - (60 * 60)) * 1000, Lib.currentTimestamp() * 1000),
                Map.of(
                    "Authorization", "bearer " + token,
                    "apikey", acbKey,
                    "Content-Type",  "application/json"
                )
            );

            if (!getHis.containsKey("data")) {
                throw new RuntimeException("Không thể lấy lịch sử giao dịch vì thiếu trường data: " + getHis.get("description"));
            }

            List<Map<String, Object>> history = (List<Map<String, Object>>) getHis.get("data");

            Collections.reverse(history);
            for (Map<String, Object> item : history) {
                if (!item.get("type").equals("IN") || !item.get("currency").equals("VND")) {
                    continue;
                }

                Long thirdId = ((Number) item.get("transactionNumber")).longValue();

                if (transactionRepository.existsByThirdId(thirdId)) {
                    continue;
                }

                String content = (String) item.get("description");
                String status = "fail";
                double amount = (double) item.get("amount");
                Long postedDate = ((Number) item.get("postingDate")).longValue() / 1000;
                
                Long userId = 0l;
                String service = "unknown";
                String note = "Không thể nhận diện dịch vụ";

                try {
                    String[] explodedPath = content.split("e6a");

                    if (explodedPath.length == 3) {
                        Long userDetect = Long.parseLong(explodedPath[1]);

                        if (userRepository.existsById(userDetect)) {
                            userId = userDetect;
                            status = "success";
                            service = "deposit";
                            note = "Nạp tiền vào ví qua ngân hàng";

                            User user = userRepository.findById(userId).orElseThrow();
                            user.setBalance(user.getBalance() + amount);
                            userRepository.save(user);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Không thể phân tách người nạp: " + e.getMessage());
                }

                //User user, Long thirdId, String content, String status, String note, double amount, Long postedDate
                try {
                    transactionRepository.save(new Transaction(userRepository.findById(userId).orElseThrow(), thirdId, service, content, status, note, amount, postedDate));
                } catch (Exception e) {
                    throw new RuntimeException("Không thể lưu giao dịch: " + e.getMessage());
                }
            }

            return getHis;
        } catch(Exception e) {
            throw new RuntimeException("Không thể lấy lịch sử giao dịch: " + e.getMessage());
        }
    }

    public Page<Transaction> list(User user, String search, Pageable pageable) {
        if (search != null && !search.isEmpty()) {
            return transactionRepository.findByUserAndNoteContainingIgnoreCase(user, search, pageable);
        }

        return transactionRepository.findByUser(user, pageable);
    }

    public void bankingWithdraw(User user, String bankType, String bankNum, Integer amount) {
        List<String> validType = new ArrayList<>(List.of("ICB", "VCB", "BIDV", "VBA", "OCB", "MBB", "TCB", "ACB", "VPB", "TPB", "STB", "HDB", "VCCB", "SCB", "VIB", "SHB", "EIB", "MSB", "CAKE", "Ubank", "SGICB", "BAB", "PVCB", "Oceanbank", "NCB", "SHBVN", "ABB", "VAB", "NAB", "PGB", "VIETBANK", "BVB", "SEAB", "COOPBANK", "LPB", "KLB", "KBank", "GPB", "CBB", "CIMB", "DBS", "DOB", "KBHCM", "KBHN", "WVN", "VRB", "SCVN", "HLBVN", "HSBC", "IBK - HN", "IBK - HCM", "IVB", "NHB HN", "UOB", "PBVN"));
        List<Integer> validAmount = new ArrayList<>(List.of(50000, 100000, 200000, 500000, 1000000));

        if (user == null) {
            throw new RuntimeException("Người dùng không hợp lệ");
        } else if (bankType == null || bankType.isEmpty() || !validType.contains(bankType)) {
            throw new RuntimeException("Ngân hàng nhận không hợp lệ");
        } else if (bankNum == null || bankNum.isEmpty()) {
            throw new RuntimeException("Số tài khoản nhận không hợp lệ");
        } else if (amount == null || amount <= 0 || !validAmount.contains(amount)) {
            throw new RuntimeException("Số tiền không hợp lệ");
        } else if (amount > user.getBalance()) {
            throw new RuntimeException("Số dư trong ví không đủ để rút số tiền này");
        }

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);

        String note = String.format("Rút tiền qua ngân hàng %s, số tài khoản: %s", bankType, bankNum);
        transactionRepository.save(new Transaction(user, 0l, "withdraw", null, "wait", note, (amount * -1), Lib.currentTimestamp()));
    }

    public void cardWithdraw(User user, String cardType, Integer amount) {
        List<String> validType = new ArrayList<>(List.of("Viettel", "Mobifone", "Vinaphone"));
        List<Integer> validAmount = new ArrayList<>(List.of(50000, 100000, 200000, 500000, 1000000));

        if (user == null) {
            throw new RuntimeException("Người dùng không hợp lệ");
        } else if (cardType == null || cardType.isEmpty() || !validType.contains(cardType)) {
            throw new RuntimeException("Nhà mạng không hợp lệ");
        } else if (amount == null || amount <= 0 || !validAmount.contains(amount)) {
            throw new RuntimeException("Số tiền không hợp lệ");
        } else if (amount > user.getBalance()) {
            throw new RuntimeException("Số dư trong ví không đủ để rút số tiền này");
        }

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);

        String note = String.format("Rút tiền qua thẻ di động %s", cardType);
        transactionRepository.save(new Transaction(user, 0l, "withdraw", null, "wait", note, (amount * -1), Lib.currentTimestamp()));
    }
}
