document.addEventListener('DOMContentLoaded', () => {

  const themeToggleButtons = document.querySelectorAll('.theme-toggle-button');

  if (themeToggleButtons.length > 0) {
    const isDarkMode = () => localStorage.getItem('theme') === 'dark';

    const updateTheme = () => {
      if (isDarkMode()) {
        document.body.classList.add('dark');
      } else {
        document.body.classList.remove('dark');
      }
    };

    themeToggleButtons.forEach(button => {
      button.addEventListener('click', () => {
        if (isDarkMode()) {
          localStorage.setItem('theme', 'light');
        } else {
          localStorage.setItem('theme', 'dark');
        }
        updateTheme();
      });
    });

    updateTheme();
  }
});

