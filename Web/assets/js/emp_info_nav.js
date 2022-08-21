

const ProfileNav = document.querySelector('#section1');
const AttendanceNav = document.querySelector('#section3');
const DavisNav = document.querySelector('#section2');
const Profile = document.querySelector('#main-container1');
const Attendance = document.querySelector('#main-container2');
const Davis = document.querySelector('#main-container3');
console.log(AttendanceNav,ProfileNav,DavisNav);
Profile.style.display = 'flex';
Attendance.style.display = 'none';
Davis.style.display = 'none';
ProfileNav.classList.add('active-nav');

AttendanceNav.addEventListener('click',() => {
    console.log('not:attendance');
    Attendance.style.display = 'flex';
    Profile.style.display = 'none';
    Davis.style.display = 'none';
    AttendanceNav.classList.add('active-nav');
    if(ProfileNav.classList.contains('active-nav'))
    {
        ProfileNav.classList.remove('active-nav');
    }
    if(DavisNav.classList.contains('active-nav'))
    {
        DavisNav.classList.remove('active-nav');
    }
});
DavisNav.addEventListener('click',() =>{
    Davis.style.display = 'inherit';
    Attendance.style.display = 'none';
    Profile.style.display = 'none';
    DavisNav.classList.add('active-nav');
    if(ProfileNav.classList.contains('active-nav'))
    {
        ProfileNav.classList.remove('active-nav');
    }
    if(AttendanceNav.classList.contains('active-nav'))
    {
        AttendanceNav.classList.remove('active-nav');
    }
});

ProfileNav.addEventListener('click',() => {
    Profile.style.display = 'flex';
    Attendance.style.display = 'none';
    Davis.style.display = 'none';
    ProfileNav.classList.add('active-nav');
    if(AttendanceNav.classList.contains('active-nav'))
    {
        AttendanceNav.classList.remove('active-nav');
    }
    if(DavisNav.classList.contains('active-nav'))
    {
        DavisNav.classList.remove('active-nav');
    }
});


