/* 메뉴 이동 */
function goMenu(url) {
  window.location.href = url;
}

$(function() {
  $(document).on('click', '#icon-gear', function(e) {
    e.preventDefault();
    e.stopPropagation();
    goMenu('/admin');
  });
});

getCookie = (name) => {
  let cookieArray = document.cookie.split(';');
  for (let i = 0; i < cookieArray.length; i++) {
    let cookiePair = cookieArray[i].split('=');
    if (name === cookiePair[0].trim()) {
      return decodeURIComponent(cookiePair[1]);
    }
  }
  return null;
}