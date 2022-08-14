

const home = document.querySelector('#emp');
const profile = document.querySelector('#analytics');
const friends = document.querySelector('#add-emp');
if(window.location.pathname == "/emp_list")
{
    try{home.parentNode.classList.add('nav-highlight')}catch{}
    try{profile.parentNode.classList.remove('nav-highlight')}catch{}
    try{friends.parentNode.classList.remove('nav-highlight')}catch{}
}
if(window.location.pathname == "/emp_overview")
{
    try{home.parentNode.classList.remove('nav-highlight')}catch{}
    try{profile.parentNode.classList.add('nav-highlight')}catch{}
    try{friends.parentNode.classList.remove('nav-highlight')}catch{}
}
if(window.location.pathname == "/create_emp")
{
    try{home.parentNode.classList.remove('nav-highlight')}catch{}
    try{profile.parentNode.classList.remove('nav-highlight')}catch{}
    try{friends.parentNode.classList.add('nav-highlight')}catch{}
}