import os
import base64
import hashlib
import requests
import webbrowser
from urllib.parse import urlparse, parse_qs

# ===== CONFIG =====
AUTH_SERVER = "http://localhost:8081"
CONTENT_SERVICE = "http://localhost:8089"
CLIENT_ID = "postgram-web"
REDIRECT_URI = "http://localhost:4200/oauth/callback"

# ===== PKCE HELPER =====
def generate_code_verifier():
    return base64.urlsafe_b64encode(os.urandom(64)).decode().rstrip("=")

def generate_code_challenge(verifier):
    digest = hashlib.sha256(verifier.encode("ascii")).digest()
    return base64.urlsafe_b64encode(digest).decode().rstrip("=")

def get_access_token():
    code_verifier = generate_code_verifier()
    code_challenge = generate_code_challenge(code_verifier)

    auth_url = (
        f"{AUTH_SERVER}/oauth2/authorize?"
        f"response_type=code"
        f"&client_id={CLIENT_ID}"
        f"&redirect_uri={REDIRECT_URI}"
        f"&scope=openid"
        f"&code_challenge={code_challenge}"
        f"&code_challenge_method=S256"
    )

    print("\n👉 Open this URL in browser to login:")
    print(auth_url)
    webbrowser.open(auth_url)

    redirected_url = input("\n👉 Paste the full redirect URL here (the one with ?code=...):\n")
    try:
        parsed = urlparse(redirected_url)
        code = parse_qs(parsed.query)["code"][0]
    except Exception as e:
        print(f"❌ Failed to parse code: {e}")
        return None

    token_url = f"{AUTH_SERVER}/oauth2/token"
    data = {
        "grant_type": "authorization_code",
        "client_id": CLIENT_ID,
        "redirect_uri": REDIRECT_URI,
        "code": code,
        "code_verifier": code_verifier,
    }

    response = requests.post(token_url, data=data)
    if response.status_code != 200:
        print(f"❌ Failed to get token: {response.text}")
        return None
    
    token_data = response.json()
    return token_data.get("access_token")

# ===== CONTENT TESTING =====
def test_content(token):
    headers = {"Authorization": f"Bearer {token}"}
    
    # 0. Check Health
    print("\n🏥 Checking service health...")
    try:
        health_resp = requests.get(f"{CONTENT_SERVICE}/api/v1/posts/health", headers=headers)
        if health_resp.status_code == 200:
            print(f"✅ Health check passed: {health_resp.text}")
        else:
            print(f"⚠️ Health check failed with status {health_resp.status_code}: {health_resp.text}")
    except Exception as e:
        print(f"❌ Could not connect to service: {e}")
        return

    # 1. Create a Post
    print("\n📝 Creating a post...")
    post_data = {
        "content": "Hello Postgram! This is a test post from content_tester.py",
        "visibility": "PUBLIC"
    }
    response = requests.post(f"{CONTENT_SERVICE}/api/v1/posts", json=post_data, headers=headers)
    if response.status_code != 201:
        print(f"❌ Create post failed: {response.status_code} {response.text}")
        return
    
    post = response.json()
    post_id = post['id']
    print(f"✅ Post created with ID: {post_id}")

    # 2. Get All Posts
    print("\n📑 Fetching all posts...")
    response = requests.get(f"{CONTENT_SERVICE}/api/v1/posts", headers=headers)
    if response.status_code == 200:
        print(f"✅ Found {len(response.json())} posts")
    else:
        print(f"❌ Get posts failed: {response.text}")

    # 3. Like the Post
    print(f"\n❤️ Liking post {post_id}...")
    response = requests.post(f"{CONTENT_SERVICE}/api/v1/posts/{post_id}/likes", headers=headers)
    if response.status_code == 201:
        print("✅ Post liked")
    else:
        print(f"❌ Like failed: {response.text}")

    # 4. Comment on the Post
    print(f"\n💬 Adding a comment to post {post_id}...")
    comment_data = {"content": "Great post! 🚀"}
    response = requests.post(f"{CONTENT_SERVICE}/api/v1/posts/{post_id}/comments", json=comment_data, headers=headers)
    if response.status_code == 201:
        print("✅ Comment added")
    else:
        print(f"❌ Comment failed: {response.text}")

    # 5. Get Comments
    print(f"\n💬 Fetching comments for post {post_id}...")
    response = requests.get(f"{CONTENT_SERVICE}/api/v1/posts/{post_id}/comments", headers=headers)
    if response.status_code == 200:
        print(f"✅ Found {len(response.json())} comments")
    else:
        print(f"❌ Get comments failed: {response.text}")

def main():
    print("🚀 Postgram Content Service Tester")
    
    choice = input("\nDo you have an access token already? (y/n): ").lower()
    if choice == 'y':
        token = input("Paste your token here: ").strip()
    else:
        token = get_access_token()
    
    if token:
        print("\n🔑 Token acquired successfully!")
        test_content(token)
    else:
        print("\n❌ No token available. Exiting.")

if __name__ == "__main__":
    main()
