

const main = document.querySelector('main');
const viss = document.querySelectorAll('.vis');
viss.forEach((vis) => {
    vis.remove();
    main.appendChild(vis);
});