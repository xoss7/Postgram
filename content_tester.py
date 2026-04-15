import requests
import hashlib
import base64
import os
import secrets
import json
import time

# Configuration
AUTH_URL = "http://localhost:8081"
GATEWAY_URL = "http://localhost:8080"
REDIRECT_URI = "http://localhost:4200/oauth/callback"
CLIENT_ID = "postgram-web"

def generate_pkce():
    code_verifier = secrets.token_urlsafe(64)
    code_challenge = base64.urlsafe_b64encode(hashlib.sha256(code_verifier.encode()).digest()).decode().replace('=', '')
    return code_verifier, code_challenge

def test_content_management(access_token):
    headers = {"Authorization": f"Bearer {access_token}", "Content-Type": "application/json"}
    
    print("\n--- Testing Content Management ---")
    
    # 1. Create a Post
    post_data = {
        "content": "Ceci est un post de test via le script!",
        "visibility": "PUBLIC"
    }
    print("Creating post...")
    response = requests.post(f"{GATEWAY_URL}/api/v1/content/posts", headers=headers, json=post_data)
    if response.status_code != 201:
        print(f"FAILED to create post: {response.status_code} - {response.text}")
        return
    
    post = response.json()
    post_id = post['id']
    print(f"SUCCESS: Post created with ID {post_id}")
    
    # 2. Like the Post
    print(f"Liking post {post_id}...")
    response = requests.post(f"{GATEWAY_URL}/api/v1/content/posts/{post_id}/like", headers=headers)
    if response.status_code == 200:
        print("SUCCESS: Post liked")
    else:
        print(f"FAILED to like post: {response.status_code}")
    
    # 3. Add a Comment
    comment_data = {"content": "Super post !"}
    print(f"Adding comment to post {post_id}...")
    response = requests.post(f"{GATEWAY_URL}/api/v1/content/posts/{post_id}/comments", headers=headers, json=comment_data)
    if response.status_code == 201:
        print("SUCCESS: Comment added")
    else:
        print(f"FAILED to add comment: {response.status_code}")
    
    # 4. Get Post Details
    print(f"Fetching post {post_id} details...")
    response = requests.get(f"{GATEWAY_URL}/api/v1/content/posts/{post_id}", headers=headers)
    if response.status_code == 200:
        details = response.json()
        print(f"Post details: Likes={details.get('likesCount')}, Comments={details.get('commentsCount')}")
    else:
        print(f"FAILED to fetch details: {response.status_code}")

    # 5. Delete Post
    print(f"Deleting post {post_id}...")
    response = requests.delete(f"{GATEWAY_URL}/api/v1/content/posts/{post_id}", headers=headers)
    if response.status_code == 204:
        print("SUCCESS: Post deleted")
    else:
        print(f"FAILED to delete post: {response.status_code}")

if __name__ == "__main__":
    print("--- Postgram Content Management Tester ---")
    
    # Step 1: PKCE
    code_verifier, code_challenge = generate_pkce()
    
    auth_url = (f"{AUTH_URL}/oauth2/authorize?response_type=code&client_id={CLIENT_ID}"
                f"&redirect_uri={REDIRECT_URI}&scope=openid%20profile%20read%20write"
                f"&code_challenge={code_challenge}&code_challenge_method=S256")
    
    print(f"\n1. Veuillez vous authentifier ici :")
    print(auth_url)
    print("\n2. Après connexion, copiez l'URL de redirection (qui contient ?code=...)")
    
    redirected_url = input("\nCollez l'URL de redirection complète ici : ").strip()
    
    if "code=" in redirected_url:
        code = redirected_url.split("code=")[1].split("&")[0]
        
        # Step 2: Exchange Code for Token
        print("\nExchanging code for token...")
        token_data = {
            "grant_type": "authorization_code",
            "code": code,
            "redirect_uri": REDIRECT_URI,
            "client_id": CLIENT_ID,
            "code_verifier": code_verifier
        }
        
        response = requests.post(f"{AUTH_URL}/oauth2/token", data=token_data)
        
        if response.status_code == 200:
            tokens = response.json()
            access_token = tokens['access_token']
            print("SUCCESS: Access token obtained")
            
            # Step 3: Run Content Tests
            test_content_management(access_token)
        else:
            print(f"FAILED to obtain token: {response.status_code} - {response.text}")
    else:
        print("URL invalide. Le paramètre 'code' est manquant.")
