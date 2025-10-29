document.addEventListener('DOMContentLoaded', () => {

  const hamburgerButton = document.getElementById('hamburger-button');
  const collapsibleMenu = document.getElementById('collapsible-menu');

  if (hamburgerButton && collapsibleMenu) {

    hamburgerButton.addEventListener('click', () => {

      collapsibleMenu.classList.toggle('is-open');

      const isExpanded = collapsibleMenu.classList.contains('is-open');
      hamburgerButton.setAttribute('aria-expanded', isExpanded);
    });

    const mediaQuery = window.matchMedia('(min-width: 875px)');

    function handleBreakpointChange(mqlEvent) {
      if (mqlEvent.matches) {
        collapsibleMenu.classList.remove('is-open');
        hamburgerButton.setAttribute('aria-expanded', 'false');
      }
    }

    mediaQuery.addEventListener('change', handleBreakpointChange);
  }
});