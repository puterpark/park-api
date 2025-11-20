/* 메뉴 이동 */
const goMenu = (url) => {
  window.location.href = url;
};

$(document).ready(() => {
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

// focus에 따라 label 노출 여부 설정
const toggleLabelOnFocus = (id) => {
  const $input = $(`#${id}`);
  $input.focusin(() => {
    $(`label[for=${id}]`).addClass('hidden');
  });
  $input.focusout(() => {
    if ($input.val().trim().length === 0) {
      $(`label[for=${id}]`).removeClass('hidden');
    }
  });
};

// X-Forced-Primary 설정
const setForcedPrimaryHeader = (headers, useHeader) => {
  if (useHeader) {
    headers['X-Forced-Primary'] = 'true';
  }
}
