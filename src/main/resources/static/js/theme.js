document.addEventListener('DOMContentLoaded', () => {
  const themeToggleBtn = document.getElementById('theme-toggle');

  if (themeToggleBtn) {
    themeToggleBtn.addEventListener('click', () => {

      if (document.body.classList.contains('dark')) {

        document.body.classList.remove('dark');
        localStorage.setItem('theme', 'light');
      } else {

        document.body.classList.add('dark');
        localStorage.setItem('theme', 'dark');
      }
    });
  }
});