

const url1 = 'https://sih-smart-attendance.herokuapp.com/get_log_data'
const data1 = {'last_n_days':300};

const url2 = 'https://sih-smart-attendance.herokuapp.com/get_user_overview'

$.ajax(url1, {
    type: 'POST',  // http method
    data: data1,  // data to submit
    success: function (data, status, xhr) {
        console.log(data);
    }
});

$.ajax(url2, {
    type: 'POST',  // http method
    success: function (data, status, xhr) {
        console.log(data);
    }
});