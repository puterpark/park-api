const refreshHoliday = () => {
  swal({
    title: '새로고침하시겠습니까?',
    icon: "warning",
    buttons: true,
    dangerMode: true,
  }).then((willRefresh) => {
    if (willRefresh) {
      loader(1);

      $.ajax({
        url: '/api/v1/admin/holiday/refresh',
        type: 'POST',
        contentType: 'application/json',
        headers: {
          'Authorization': 'Bearer ' + getCookie('accessToken')
        },
        success: (succ) => {
          if (succ.code === 'S0000') {
            swal('완료되었습니다.', '', 'success');
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
  });

}