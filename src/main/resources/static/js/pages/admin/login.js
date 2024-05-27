enterCheck = () => {
  if (event.keyCode === 13) {
    login();
  }
}

login = () => {
  const username = $('#username').val();
  const password = $('#password').val();

  if (!username || !password) {
    swal('입력된 값이 없습니다.', '', 'error');
    return;
  }

  loader(1);

  $.ajax({
    url: '/api/v1/member/login',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
      username,
      password
    }),
    success: (succ) => {
      if (succ.code === 'S0000') {
        document.cookie = `accessToken=${succ.data.accessToken}; path=/`;
        location.href = '/admin/shorten-url';
      }
    },
    error: (err) => {
      const res = err.responseJSON;
      if (res) {
        const err_msg = GLOBAL_ERR_MSG[res.code];
        if (err_msg) {
          swal(`${err_msg}`, `${res.data ?? ''}`, 'error');
        }
      } else {
        swal(GLOBAL_ERR_MSG.E0003, '', 'error');
      }
    },
    complete: () => {
      loader(0);
    }
  });
}