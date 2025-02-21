$(document).ready(() => {
  toggleLabelOnFocus('plain-text');
  toggleLabelOnFocus('secret-key');
  toggleLabelOnFocus('iv');
  toggleLabelOnFocus('return-text');
});

// 암호화
const encrypt = () => {
  const plainText = $('#plain-text').val().trim();
  const secretKey = $('#secret-key').val().trim();
  const iv = $('#iv').val().trim();
  const useBase64 = $('#use-base64').is(':checked') ? 'Y' :'N';

  if (!plainText || !secretKey) {
    swal('입력된 값을 확인해 주세요.', '', 'error');
    return;
  }

  if (!validSecretKey(secretKey)) {
    swal('비밀 키', '16/24/32자리의 영문자, 숫자, 특수문자만 사용할 수 있습니다.', 'error');
    return;
  }

  if (iv && !validIv(iv)) {
    swal('iv', '16자리 영문자, 숫자, 특수문자만 사용할 수 있습니다.', 'error');
    return;
  }

  loader(1);

  $.ajax({
    url: '/api/v1/aes/encrypt',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
      plainText,
      secretKey,
      iv,
      useBase64
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
  const iv = $('#iv').val().trim();
  const useBase64 = $('#use-base64').is(':checked') ? 'Y' :'N';

  if (!encText || !secretKey) {
    swal('입력된 값을 확인해 주세요.', '', 'error');
    return;
  }

  if (!validSecretKey(secretKey)) {
    swal('비밀 키', '영문자, 숫자, 특수문자만 사용할 수 있습니다.', 'error');
    return;
  }

  if (iv && !validIv(iv)) {
    swal('iv', 'iv는 16자리 영문자, 숫자, 특수문자만 사용할 수 있습니다.', 'error');
    return;
  }

  loader(1);

  $.ajax({
    url: '/api/v1/aes/decrypt',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({
      encText,
      secretKey,
      iv,
      useBase64
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

const validSecretKey = (str) => {
  const length = str.length;
  if (length !== 16 && length !== 24 && length !== 32) {
    return false;
  }
  return /^[A-Za-z0-9!@#$%^&*()_+={}\[\]:;"'<>,.?/~`-]*$/.test(str);
};

const validIv = (str) => {
  return /^[A-Za-z0-9!@#$%^&*()_+={}\[\]:;"'<>,.?/~`-]{16}$/.test(str);
}

const copy = () => {
  const $returnText = $('#return-text');
  const val = $returnText.val();
  if (val.trim().length === 0) {
    return;
  }
  $returnText.select();
  document.execCommand('copy');
  swal('복사되었습니다.', '', 'success');
};
