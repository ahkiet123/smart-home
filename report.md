# 📋 PRODUCT BACKLOG - SMART HOME

Tất cả các chức năng đã được:
- Gộp các thẻ trùng logic
- Gộp checklist từ Module
- Chuẩn hóa tên thẻ
- Không bỏ sót bất kỳ task nào
- Sắp xếp theo thứ tự ưu tiên triển khai

---

# PRODUCT BACKLOG

---

## AUTH - Quên mật khẩu

User Story:  
Là một người dùng, tôi muốn đặt lại mật khẩu bằng OTP để có thể truy cập lại hệ thống.

Checklist:

- [ ] Gửi mã OTP
- [ ] Xác thực OTP
- [ ] Cho phép nhập mật khẩu mới
- [ ] Test chức năng

---

## AUTH - Quản lý thông tin cá nhân

User Story:  
Là một người dùng, tôi muốn xem và cập nhật thông tin cá nhân.

Checklist:

- [ ] Tạo màn hình Profile
- [ ] Tạo API lấy thông tin user
- [ ] Tạo API cập nhật profile
- [ ] Hiển thị dữ liệu user
- [ ] Kiểm tra cập nhật dữ liệu

---

## HOME - Bảng tổng quan tiêu thụ

User Story:  
Là người dùng, tôi muốn xem tổng điện năng tiêu thụ.

Checklist:

- [ ] Hiển thị tổng kWh
- [ ] Hiển thị tổng tiền điện
- [ ] Kết nối API
- [ ] Hiển thị danh sách bài viết
- [ ] Xử lý không có dữ liệu
- [ ] Kiểm tra dữ liệu chính xác

---

## HOME - Điều hướng tab

User Story:  
Là người dùng, tôi muốn chuyển giữa các tab.

Checklist:

- [ ] Thiết kế UI 3 tab
- [ ] Xử lý chuyển tab
- [ ] Load dữ liệu theo tab
- [ ] Highlight tab active
- [ ] Không reload toàn trang

---

## ROOM - Danh sách phòng

(Gộp từ room + module room)

User Story:  
Là người dùng, tôi muốn xem danh sách phòng.

Checklist:

- [ ] Tạo API lấy danh sách phòng
- [ ] Hiển thị tên phòng
- [ ] Hiển thị tổng điện năng phòng
- [ ] Xử lý click phòng
- [ ] Kiểm tra dữ liệu

---

## ROOM - Chi tiết phòng

User Story:  
Là người dùng, tôi muốn xem chi tiết phòng.

Checklist:

- [ ] Gọi API lấy thiết bị theo phòng
- [ ] Hiển thị tên thiết bị
- [ ] Hiển thị mức tiêu thụ điện
- [ ] Thiết kế UI danh sách thiết bị
- [ ] Click thiết bị
- [ ] Điều hướng sang Statistics
- [ ] Kiểm tra dữ liệu

---

## ROOM - Điều hướng sang thống kê

User Story:  
Là người dùng, tôi muốn chuyển sang thống kê từ phòng.

Checklist:

- [ ] Click phòng → Statistics
- [ ] Click thiết bị → Statistics
- [ ] Truyền dữ liệu
- [ ] Load đúng dữ liệu
- [ ] Kiểm tra điều hướng

---

## ROOM - Xóa phòng

User Story:  
Là quản trị viên, tôi muốn xóa phòng.

Checklist:

- [ ] Tạo API xóa phòng
- [ ] Xử lý thiết bị liên quan

---

## DEVICE - Quản lý thiết bị

(Gộp thêm + xem + cập nhật + xóa)

User Story:  
Là người dùng, tôi muốn quản lý dữ liệu thiết bị để phục vụ thống kê và tính điện năng.

Checklist:

- [ ] Tạo Entity Device
- [ ] Tạo màn hình thêm thiết bị
- [ ] Tạo API thêm thiết bị
- [ ] Kiểm tra công suất thiết bị
- [ ] Lưu database
- [ ] Tạo API lấy danh sách thiết bị
- [ ] Hiển thị thiết bị
- [ ] Tạo API cập nhật thiết bị
- [ ] Cập nhật giao diện
- [ ] Tạo API xóa thiết bị
- [ ] Hiển thị xác nhận xóa
- [ ] Ẩn toàn bộ chức năng bật/tắt thiết bị
- [ ] Hiển thị trạng thái dữ liệu của thiết bị thay vì trạng thái ON/OFF
- [ ] Gắn thiết bị vào phòng để phục vụ thống kê

---

## DEVICE - Ghi nhận thời gian sử dụng

User Story:  
Là người dùng, tôi muốn lưu thời gian sử dụng của thiết bị để hệ thống tính được điện năng tiêu thụ.

Checklist:

- [ ] Tạo Entity DeviceUsageLog
- [ ] Tạo API thêm bản ghi sử dụng
- [ ] Tạo API lấy lịch sử sử dụng theo thiết bị
- [ ] Cho phép nhập thời gian bắt đầu và kết thúc hoặc số giờ sử dụng
- [ ] Validate dữ liệu thời gian không âm, không trùng logic
- [ ] Hiển thị lịch sử sử dụng ở màn chi tiết thiết bị
- [ ] Dùng dữ liệu này cho phần ước tính kWh và tiền điện

---

## DEVICE - Ước tính điện năng tiêu thụ

User Story:  
Là người dùng, tôi muốn ước tính điện năng tiêu thụ của từng thiết bị dựa trên công suất và thời gian sử dụng.

Checklist:

- [ ] Tạo công thức tính kWh theo công suất và thời gian sử dụng
- [ ] Tạo API ước tính điện năng theo thiết bị
- [ ] Hiển thị kết quả ước tính ở màn chi tiết thiết bị
- [ ] Kiểm tra dữ liệu đầu vào hợp lệ
- [ ] Lấy dữ liệu từ DeviceUsageLog hoặc input thời gian sử dụng
- [ ] Hiển thị công thức tính rõ trên UI để dev và người dùng dễ kiểm tra

---

## ENERGY - Tính toán điện năng tiêu thụ

(Gộp từ: energy + module energy)

User Story:  
Là hệ thống, tôi muốn tính toán điện năng tiêu thụ để các module hiển thị dữ liệu chính xác.

Checklist:

- [ ] Tạo Entity EnergyRecord
- [ ] Tạo API tính điện năng
- [ ] Triển khai công thức kWh  
  kWh = Watt × Hours / 1000
- [ ] Triển khai công thức tính tiền điện (VND)
- [ ] Tính chi phí điện
- [ ] Lưu dữ liệu
- [ ] Áp dụng cho cấp Home
- [ ] Áp dụng cho cấp Room
- [ ] Áp dụng cho Statistics
- [ ] Kiểm tra logic chính xác

---

## ENERGY - Xem điện năng theo ngày

(Gộp từ: energy + statistics)

User Story:  
Là một người dùng, tôi muốn xem điện năng theo ngày.

Checklist:

- [ ] Tạo API điện năng theo ngày
- [ ] Hiển thị biểu đồ ngày
- [ ] Hiển thị tổng kWh
- [ ] Tích hợp thư viện biểu đồ
- [ ] Hiển thị danh sách phòng
- [ ] Xử lý khi không có dữ liệu

---

## ENERGY - Xem điện năng theo tháng

User Story:  
Là một người dùng, tôi muốn xem điện năng theo tháng.

Checklist:

- [ ] Tạo API điện năng tháng
- [ ] Hiển thị biểu đồ tháng
- [ ] Kiểm tra dữ liệu hiển thị

---

## ENERGY - Cập nhật giá điện

User Story:  
Là quản trị viên, tôi muốn cập nhật giá điện.

Checklist:

- [ ] Tạo Entity ElectricityPrice
- [ ] Tạo API cập nhật giá điện
- [ ] Tạo màn hình Settings
- [ ] Kiểm tra cập nhật

---

## STATISTICS - Biểu đồ theo phòng

User Story:  
Là người dùng, tôi muốn xem điện năng theo phòng.

Checklist:

- [ ] Hiển thị biểu đồ theo phòng
- [ ] Gọi API lấy dữ liệu
- [ ] Hiển thị danh sách thiết bị
- [ ] Kiểm tra dữ liệu

---

## STATISTICS - Xếp hạng thiết bị tiêu thụ

User Story:  
Là người dùng, tôi muốn xem thiết bị tiêu thụ nhiều nhất.

Checklist:

- [ ] Tính mức tiêu thụ thiết bị
- [ ] Sắp xếp giảm dần
- [ ] Hiển thị danh sách thiết bị
- [ ] Kiểm tra dữ liệu

---

## DASHBOARD - Hiển thị thiết bị tiêu thụ nhiều nhất

User Story:  
Là người dùng, tôi muốn xem thiết bị tiêu thụ điện nhiều nhất.

Checklist:

- [ ] Tính top thiết bị
- [ ] Hiển thị danh sách

---

## DASHBOARD - Cảnh báo tiêu thụ cao

User Story:  
Là người dùng, tôi muốn nhận cảnh báo tiêu thụ bất thường.

Checklist:

- [ ] Kiểm tra tiêu thụ bất thường
- [ ] Hiển thị cảnh báo

---

## ANALYTICS - Phân tích điện năng

User Story:  
Là người dùng, tôi muốn xem phân tích điện năng.

Checklist:

- [ ] Tạo màn hình analytics
- [ ] Hiển thị biểu đồ
- [ ] Phân tích theo phòng

---

## RECOMMENDATION - Gợi ý tiết kiệm điện

(Gộp tạo + xem)

User Story:  
Là người dùng, tôi muốn xem gợi ý tiết kiệm điện.

Checklist:

- [ ] Tạo Entity Recommendation
- [ ] Xây dựng logic gợi ý
- [ ] Tính tiền tiết kiệm dự kiến
- [ ] Lưu dữ liệu
- [ ] Tạo API lấy gợi ý
- [ ] Hiển thị danh sách

---

## REPORT - Xuất báo cáo tháng

User Story:  
Là người dùng, tôi muốn xuất báo cáo điện năng.

Checklist:

- [ ] Tạo báo cáo
- [ ] Xuất PDF

---

## BLOG - Danh sách bài viết

(Gộp module blog)

User Story:  
Là người dùng, tôi muốn xem danh sách bài viết.

Checklist:

- [ ] Gọi API lấy danh sách bài viết
- [ ] Hiển thị tiêu đề + mô tả ngắn
- [ ] Thiết kế UI
- [ ] Click chuyển trang chi tiết
- [ ] Xử lý không có dữ liệu
- [ ] Kiểm tra điều hướng

---

## BLOG - Chi tiết bài viết

User Story:  
Là người dùng, tôi muốn xem nội dung bài viết.

Checklist:

- [ ] Hiển thị tiêu đề
- [ ] Hiển thị nội dung
- [ ] Hiển thị ngày đăng
- [ ] Nút quay lại
- [ ] Kiểm tra dữ liệu

---

## BLOG - Quản lý bài viết

(Gộp tạo + cập nhật + xóa)

User Story:  
Là admin, tôi muốn quản lý bài viết.

Checklist:

- [ ] Tạo form viết bài
- [ ] Tạo API tạo blog
- [ ] Upload hình ảnh
- [ ] Tạo API cập nhật blog
- [ ] Tạo API xóa blog

---

## BLOG - Quản lý danh mục

User Story:  
Là admin, tôi muốn quản lý danh mục blog.

Checklist:

- [ ] Tạo Entity BlogCategory
- [ ] Tạo API lấy danh mục
