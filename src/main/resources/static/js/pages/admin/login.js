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
      processError(err);
    },
    complete: () => {
      loader(0);
    }
  });
}