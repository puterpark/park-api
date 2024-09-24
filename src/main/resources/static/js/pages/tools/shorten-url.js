Mousetrap.bind('ctrl+enter', function() {
  shorten();
});

function enterCheck() {
  if (event.keyCode === 13) {
    shorten();
  }
}

function shorten() {
  const originalUrl = $('#original-url').val().trim();

  $('#resultDiv').addClass('hidden');

  if (!originalUrl || originalUrl.length === 0) {
    swal('입력된 값이 없습니다.', '', 'error');
    return;
  }

  var regExr = /^(https?:\/\/)([\w\d-_]+)\.([\w\d-_\.]+)\/?\??([^#\n\r]*)?#?([^\n\r]*)/;
  if (!regExr.test(originalUrl)) {
    swal('URL 형식이 아닙니다.', '', 'error');
    return;
  }

  loader(1);

  $.ajax({
    url: '/api/v1/shorten-url',
    type: 'post',
    contentType: 'application/json',
    data: JSON.stringify({
      orgUrl: originalUrl
    }),
    success: (succ) => {
      if (succ.code === 'S0000') {
        const data = succ.data;
        let shortenUrl = `${window.location.protocol}//${window.location.hostname}`;
        if (window.location.port !== '') {
          shortenUrl += `:${window.location.port}`;
        }
        shortenUrl += `/${data.shortenUri}`;
        $('#resultDiv').empty().append(`<div class='widget widget5 card' onclick='copy()'><div class='widget-content p-4'><div class='row'><div class='col-12' style='display: flex;'><div class='col' style='padding-top: 10px;'><h4 id='shorten-url'>${shortenUrl}</h4></div></div></div></div></div>`);
        $('#resultDiv').removeClass('hidden');
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

function copy() {
  $('#tmpForCopy').val($('#shorten-url').text());
  $('#tmpForCopy').select();
  document.execCommand('copy');
  swal('복사되었습니다.', '', 'success');
}