/* 메뉴 이동 */
const goMenu = (url) => {
  window.location.href = url;
};

$(function() {
  $(document).on('click', '#icon-gear', function(e) {
    e.preventDefault();
    e.stopPropagation();
    goMenu('/admin');
  });
});

const getCookie = (name) => {
  let cookieArray = document.cookie.split(';');
  for (let i = 0; i < cookieArray.length; i++) {
    let cookiePair = cookieArray[i].split('=');
    if (name === cookiePair[0].trim()) {
      return decodeURIComponent(cookiePair[1]);
    }
  }
  return null;
};

// 에러 처리
const processError = (error) => {
  const res = error.responseJSON;
  if (res) {
    const err_msg = GLOBAL_ERR_MSG[res.code];
    if (err_msg) {
      swal(`${err_msg}`, `${res.data ?? ''}`, 'error');
    } else {
      swal(GLOBAL_ERR_MSG.E0003, '', 'error');
    }
  } else {
    swal(GLOBAL_ERR_MSG.E0003, '', 'error');
  }
};