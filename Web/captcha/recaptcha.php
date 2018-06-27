<?php
class addon_recaptcha
{
    function hook_register_after_validation()
    {
        global $errors;

        if (empty($errors) && !$this->verify_user_response())
        {
            $errors[] = 'Please prove that you are human.';
        }
    }

    function hook_register_before_submit()
    {
        global $luna_config;

        $site_key = $luna_config['recaptcha_site_key'];

?>
        <div class="form-group">
			<div class="col-sm-12">
				<h4>Are you a human?</h4>
				<hr class="draw-line"/>
				<p>Please prove that you are a human!</p>
				<script src='https://www.google.com/recaptcha/api.js'></script>
				<div class="g-recaptcha" data-sitekey="<?php echo luna_htmlspecialchars($site_key) ?>"></div>
			</div>
        </div>
<?php
    }

    function verify_user_response()
    {
        global $luna_config;

        if (empty($_POST['g-recaptcha-response'])) return false;

        $secret = $luna_config['recaptcha_secret_key'];
        $response = $_POST['g-recaptcha-response'];
        $ip = get_remote_address();

        $query = "secret=$secret&response=$response&remoteip=$ip";
        $url = "https://www.google.com/recaptcha/api/siteverify?$query";

        $response = $this->send_request($url);

        return strpos($response, '"success": true') !== false;
    }

    function send_request($url)
    {
        if (function_exists('curl_version'))
            return $this->send_curl_request($url);
        else
            return $this->get_remote_file($url);
    }

    function send_curl_request($url)
    {
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE);
        $response = curl_exec($ch);
        curl_close($ch);

        return $response;
    }
    
    function get_remote_file($url)
    {
        $response = file_get_contents($url);

        if ($response === false)
            throw new Exception('Cannot validate reCAPTCHA submission.');

        return $response;
    }
}
