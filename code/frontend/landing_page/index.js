const hero = document.querySelector('.hero');
const header = document.querySelector('header');

const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (!entry.isIntersecting) {
      header.classList.add('glass');
    } else {
      header.classList.remove('glass');
    }
  });
}, {
  threshold: 0
});

observer.observe(hero);