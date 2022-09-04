package com.totalrecon.ipravesh;

public class Constant {

    // all numeric constants

    public static final int vibrate_time = 4000;
    public static final int delay_time = 2000;
    public static final int model_delay_time = 2000;
    public static final int thread_sleep_time = 100;
    public static final int turns_facedetection = 2;
    public static final int blinks_facedetection = 3;
    public static final int location_setinterval = 5000;
    public static final int location_setfastestinterval = 2000;
    public static final int location_setsmallestdisplacement = 10; // m
    public static final double location_zero_error = 30 * 0.001; // km
    public static final double location_radius = 10000000; // km
    public static final int radius_of_earth = 6371; // km
    public static final int action_time_facedetection = 6000;
    public static final int facedetection_img_width = 640;
    public static final int facedetection_img_height = 480;
    public static final int last_n_days = 30;
    public static final int splash_time = 2000;
    public static final int head_angle_y = 30;
    public static final int compressed_image_quality = 75;
    public static final double right_eye_probability_true = 0.7;
    public static final double right_eye_probability_false = 0.1;
    // all toast and dialog box messages

    public static final String attendance_msg = "Take Attendance";
    public static final String entry_attendance_msg = "Recording your entry attendance now!";
    public static final String exit_attendance_msg = "Recording your exit attendance now!";
    public static final String face_not_match_msg = "Face doesn't match!";
    public static final String face_many_msg = "There are many faces!";
    public static final String face_many_msg_formal = "Multiple faces detected! Please try again.";
    public static final String face_no_msg_formal = "No face detected! Please try again.";
    public static final String face_no_msg = "There are no faces!";
    public static final String gps_turned_on_msg = "GPS is already turned on";
    public static final String not_in_office_msg = "Sorry, you are not in your assigned office!";
    public static final String attendance_recorded_msg = "Your attendance is recorded!";
    public static final String camera_permission_granted_msg = "camera permission granted";
    public static final String camera_permission_denied_msg = "camera permission denied";
    public static final String time_exceeded_msg = "Time exceeded!";
    public static final String timer_tryagain_msg = "try again";
    public static final String failed_toadd_msg = "Failed to add";
    public static final String good_morning_msg = "Good morning!";
    public static final String good_afternoon_msg = "Good afternoon!";
    public static final String good_evening_msg = "Good evening!";
    public static final String logout_successful_msg = "Logged out successfully!";
    public static final String login_successful_msg = "You have been logged in! ";
    public static final String signup_successful_msg = "Successfully signed up";
    public static final String inside_office_msg = "Great, you are inside your office!";
    public static final String wrong_otp_msg = "Sorry, wrong otp!";
    public static final String server_error_msg = "Server Error";
    public static final String username_alreadytaken_error_msg = "This username is already taken! Please enter another username.";
    public static final String otp_sent_msg = "OTP has been resend to your email!";
    public static final String nonempty_user_pass_error_msg = "Please enter a non-empty username and password.";
    public static final String empno_empty_error_msg = "Please enter your employee number.";
    public static final String confirmpass_nomatch_error_msg = "Confirm password does not match with new password!";
    public static final String already_signup_error_msg = "You have already been signed up! Please proceed to login.";
    public static final String invalid_empno_error_msg = "Sorry, the given employee number is invalid!";
    public static final String username_empty_error_msg = "Please enter a proper username and password !";
    public static final String username_noexist_msg = "Username does not exist!";
    public static final String wrong_password_msg = "Your password is wrong!";
    public static final String already_logged_in_msg = "You have already logged in through a device!";

    // other textview and color constants

    public static final String present_text = "present";
    public static final String present_color = "#28fc03";
    public static final String absent_text = "absent";
    public static final String absent_color = "#fc2403";

    // model name
    public static final String model_name = "mobile_face_net.tflite";

    // post request: url - string constants

    public static final String login_url = "https://sih-smart-attendance.herokuapp.com/login";
    public static final String check_in_out_status_url = "https://sih-smart-attendance.herokuapp.com/check_in_out_status";
    public static final String update_log_url = "https://sih-smart-attendance.herokuapp.com/update_log";
    public static final String get_log_data_url = "https://sih-smart-attendance.herokuapp.com/get_log_data";
    public static final String get_img_url = "https://sih-smart-attendance.herokuapp.com/get_img";
    public static final String check_otp_url = "https://sih-smart-attendance.herokuapp.com/check_otp";
    public static final String send_otp_url = "https://sih-smart-attendance.herokuapp.com/send_otp";
    public static final String get_branch_info_url = "https://sih-smart-attendance.herokuapp.com/get_branch_info";
    public static final String check_emp_no_url = "https://sih-smart-attendance.herokuapp.com/check_emp_no";
    public static final String check_username_url = "https://sih-smart-attendance.herokuapp.com/check_username";
    public static final String signup_url = "https://sih-smart-attendance.herokuapp.com/signup";
    public static final String get_info_url = "https://sih-smart-attendance.herokuapp.com/get_info";
    public static final String get_embed_url = "https://sih-smart-attendance.herokuapp.com/get_embed";
}
