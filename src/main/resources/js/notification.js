function hideElement(id) {
  var elementToHide = document.getElementById(id);
  if (elementToHide != null) {
    elementToHide.style.display = "none";
  }
}
setTimeout(hideElement, 10000, "errors")
setTimeout(hideElement, 10000, "successes")