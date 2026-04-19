import os
import base64
import hashlib
import requests
import webbrowser
from urllib.parse import urlparse, parse_qs

# ===== CONFIG =====
AUTH_SERVER = "http://localhost:8081"
CLIENT_ID = "postgram-web"
REDIRECT_URI = "http://localhost:4200/oauth/callback"


# ===== PKCE =====
def generate_code_verifier():
    verifier = base64.urlsafe_b64encode(os.urandom(64)).decode().rstrip("=")
    return verifier


def generate_code_challenge(verifier):
    digest = hashlib.sha256(verifier.encode("ascii")).digest()
    challenge = base64.urlsafe_b64encode(digest).decode().rstrip("=")
    return challenge


# ===== MAIN FLOW =====
def main():
    code_verifier = generate_code_verifier()
    code_challenge = generate_code_challenge(code_verifier)

    print("code_verifier:", code_verifier)
    print("code_challenge:", code_challenge)

    # Step 1: Authorization URL
    auth_url = (
        f"{AUTH_SERVER}/oauth2/authorize?"
        f"response_type=code"
        f"&client_id={CLIENT_ID}"
        f"&redirect_uri={REDIRECT_URI}"
        f"&scope=openid"
        f"&code_challenge={code_challenge}"
        f"&code_challenge_method=S256"
    )

    print("\n👉 Open this URL in browser:")
    print(auth_url)

    webbrowser.open(auth_url)

    # Step 2: récupérer le code manuellement
    redirected_url = input("\n👉 Paste the full redirect URL here:\n")

    parsed = urlparse(redirected_url)
    code = parse_qs(parsed.query)["code"][0]

    print("authorization_code:", code)

    # Step 3: échange token
    token_url = f"{AUTH_SERVER}/oauth2/token"

    data = {
        "grant_type": "authorization_code",
        "client_id": CLIENT_ID,
        "redirect_uri": REDIRECT_URI,
        "code": code,
        "code_verifier": code_verifier,
    }

    response = requests.post(token_url, data=data)

    print("\n🎉 Token response:")
    print(response.json())


if __name__ == "__main__":
    main()
