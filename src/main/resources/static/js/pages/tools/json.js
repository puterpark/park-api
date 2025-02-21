Mousetrap.bind('ctrl+enter', function() {
  convert(0);
});
Mousetrap.bind('ctrl+shift+enter', function() {
  convert(1);
});

$(document).ready(() => {
  toggleLabelOnFocus('textarea');
});

const convert = (mode) => {
  let val = $('#textarea').val().trim();

  if (!val || val === '' || val.length === 0) {
    swal('입력된 값이 없습니다.', '', 'error');
    return;
  }

  try {
    if (mode === 0) {
      val = JSON.stringify(JSON.parse(val), null, '  ');
    } else if (mode === 1) {
      val = JSON.stringify(JSON.parse(val));
    }
  } catch (e) {
    swal('다시 입력해 주세요.', e.message, 'error');
    return;
  }

  const $resultDiv = $('#resultDiv');

  $resultDiv.empty().append("<div class='widget widget5 card'><div class='widget-content p-4'><div class='row'><div class='col-12 col-lg-6'><div id='result' class='form-group' style='display: flex;'></div></div><div class='col-12 col-lg-6' id='copy'></div></div></div></div>");
  $('#result').empty().append("<textarea class='form-control' id='output' rows='15' onclick='copy()'>" + val + "</textarea>");
  $('#copy').empty().append("<div class='alert alert-success' role='alert'><h5 class='alert-heading'>결과를 클릭하면 복사됩니다.</h5></div>");
  $resultDiv.removeClass('hidden');

  document.getElementById('resultDiv').scrollIntoView();
};

const copy = () => {
  $('#output').select();
  document.execCommand('copy');
  swal('복사되었습니다.', '', 'success');
};