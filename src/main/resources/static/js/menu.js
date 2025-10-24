// Wait for the DOM to be fully loaded before running the script
document.addEventListener('DOMContentLoaded', () => {

  // Find the hamburger button by its ID
  const hamburgerButton = document.getElementById('hamburger-button');

  // Find the collapsible menu by its ID
  const collapsibleMenu = document.getElementById('collapsible-menu');

  // Check if both elements exist on the page
  if (hamburgerButton && collapsibleMenu) {

    // Add a click event listener to the hamburger button
    hamburgerButton.addEventListener('click', () => {

      // Toggle the 'is-open' class on the menu
      collapsibleMenu.classList.toggle('is-open');

      // Update the aria-expanded attribute for accessibility
      const isExpanded = collapsibleMenu.classList.contains('is-open');
      hamburgerButton.setAttribute('aria-expanded', isExpanded);
    });
  }
});
