$(function() {
  const $plainText = $('#plain-text');
  $plainText.focusin(function() {
    $('label[for="plain-text"]').addClass('hidden');
  });
  $plainText.focusout(function() {
    if ($plainText.val().trim().length === 0) {
      $('label[for="plain-text"]').removeClass('hidden');
    }
  });

  const $secretKey = $('#secret-key');
  $secretKey.focusin(function() {
    $('label[for="secret-key"]').addClass('hidden');
  });
  $secretKey.focusout(function() {
    if ($secretKey.val().trim().length === 0) {
      $('label[for="secret-key"]').removeClass('hidden');
    }
  });

  const $returnText = $('#return-text');
  $returnText.focusin(function() {
    $('label[for="return-text"]').addClass('hidden');
  });
  $returnText.focusout(function() {
    if ($returnText.val().trim().length === 0) {
      $('label[for="return-text"]').removeClass('hidden');
    }
  });
});

// 암호화
const encrypt = () => {
  const plainText = $('#plain-text').val().trim();
  const secretKey = $('#secret-key').val().trim();

  if (!plainText || !secretKey) {
    swal('입력된 값을 확인해 주세요.', '', 'error');
    return;
  }

  if (containsHangeul(secretKey)) {
    swal('비밀 키에 한글을 사용할 수 없습니다.', '', 'error');
    return;
  }

  loader(1);

  $.ajax({
    url: '/api/v1/jasypt/encrypt',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
      plainText,
      secretKey
    }),
    success: (succ) => {
      if (succ.code === 'S0000') {
        const $returnText = $('#return-text');
        $returnText.focus();
        $returnText.val(succ.data.encText);
      }
    },
    error: (err) => {
      processError(err);
    },
    complete: () => {
      loader(0);
    }
  });
};

// 복호화
const decrypt = () => {
  const encText = $('#plain-text').val().trim();
  const secretKey = $('#secret-key').val().trim();

  if (!encText || !secretKey) {
    swal('입력된 값을 확인해 주세요.', '', 'error');
    return;
  }

  if (containsHangeul(secretKey)) {
    swal('비밀 키에 한글을 사용할 수 없습니다.', '', 'error');
    return;
  }

  loader(1);

  $.ajax({
    url: '/api/v1/jasypt/decrypt',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
      encText,
      secretKey
    }),
    success: (succ) => {
      if (succ.code === 'S0000') {
        const $returnText = $('#return-text');
        $returnText.focus();
        $returnText.val(succ.data.decText);
      }
    },
    error: (err) => {
      processError(err);
    },
    complete: () => {
      loader(0);
    }
  });
};

// 한글 포함 여부
const containsHangeul = (str) => {
  return /[ㄱ-ㅎㅏ-ㅣ가-힣]/.test(str);
};
