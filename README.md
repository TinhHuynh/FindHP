
# FindHP

**Screen shots**

- ![alt text](https://i.imgur.com/00AlTcA.png)
- ![alt text](https://i.imgur.com/BEF0e6I.png)
- ![alt text](https://i.imgur.com/E8PO7tQ.png)
- ![alt text](https://i.imgur.com/vzKX3bT.png)
- ![alt text](https://i.imgur.com/lU5NVGJ.png)
- ![alt text](https://i.imgur.com/lH4IWH7.png)

## 1.Giới thiệu
### 1.1.Mô tả:

- **FindHP** là một ứng dụng mobile giúp cho người dùng xác định vị trí các dịch vụ về sức khỏe: bệnh viện, nhà thuốc, gyms,.. xung quanh khu vực hiện tại của user hoặc khu vực được chỉ định, đồng thời cung cấp cho người dùng khoảng cách, thời gian đến đỉa điểm cũng như thông tin về nơi đó.

- Ứng dụng được phát triển trên Android.

### 1.2.API và thư viện:
#### 1.2.1.API:

- **Google Places API (Places Api):**
[Google Places API (Places Api)](https://developers.google.com/places/web-service/)
  - Tìm vị trí.
  - Thu kết quả.
  - Lấy thông tin về nơi đó.

- **Google Map API (Direction API):**
[Google Map API (Direction API)](https://developers.google.com/maps/documentation/directions/)
  - Nhận hướng đi, thời gian và khoảng cách giữa hai địa điểm.

#### 1.2.2.Libraries:

- **Retrofit 2.x** : Sử dụng các API service thông qua giao thức HTTP với kiểu dữ liệu định trước cho Android và Java
[Retrofit 2](http://square.github.io/retrofit/)
  - Hiểu quả khi sử dụng cùng các RESTful API, services: dễ thiết lập, triển khai
  - Giảm thiểu thời gian chờ trả về response từ API/services.
  - Hỗ trợ JSON-parsing, không hard-code
  - Có lợi cho &#39;Security&#39; và &#39;Performance&#39;

- **Picasso** : Thư viện cho việc tải ảnh và cache cho Android
Định vị, xác định ví trí chỉ định (hoặc vị trí hiện tại của User).
[Picasso](http://square.github.io/picasso/)
  - Dễ thiết lập, sử dụng
  - Tải hình ảnh mượt và nhanh, đặc biệt trong load ảnh từ Internet
  - Hỗ trợ Cache hình ảnh, không cần tải lại – tốt cho bộ nhớ, hiệu suất máy.

- **Butterknife** : Bind view với biến trong activity
[Butterknife](https://github.com/JakeWharton/butterknife)
  - Không cần phải gọi hàm findViewById() nhiều lần

#### 1.2.3.Ngoài ra:
App còn sử dụng các source code từ các dự án mã nguồn mở trên github:

- **BloodRouteDrawer:** (Project Go-ixe)
  - Vẽ animation đường đi giữa các địa điểm được chỉ định trên map
  
- **Google Directions Android:**
[https://github.com/jd-alexander/Google-Directions-Android](https://github.com/jd-alexander/Google-Directions-Android)
  - Package routing cho việc lấy đường đi, khoảng cách giữa 2 location.
  
- **PlaceAutoCompleteAdapter:**
[https://github.com/jd-alexander/Google-Directions-Android](https://github.com/jd-alexander/Google-Directions-Android)
  - Tự hoàn thành địa điểm.

## 2.Chức năng

- Cho phép user tìm địa điểm hoặc tự định vị vị trí của user làm vị trí chỉ định và hiện kết quả quanh vị trí đó.
- Cho phép user xác định một kiểu kết quả (nhà thuốc, bệnh viện, nha sĩ, phòng gym, …) trên bản đồ.
- Hiển thị kết quả là các marker đánh dấu xung quanh vị trí chỉ định trên bản đồ.
- Hiển thị đường đi, khoảng cách, thời gian từ vị trí chỉ định đến địa điểm mong muốn.
- Hiển thị thông tin về địa chỉ, số điện thoại, trang web, hình ảnh, thời gian làm việc, review, … của địa điểm mong muốn.
- Mở Google map trên thiết bị của user để dẫn đường tốt hơn.
- Lưu địa điểm mong muốn vào danh sách &#39;Favorite&#39;.




