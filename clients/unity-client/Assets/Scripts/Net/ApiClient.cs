using System.Collections.Generic;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using UnityEngine;
using UnityEngine.Networking;

namespace Watcher.Net
{
    /// <summary>
    /// REST client for account/character actions. The backend is authoritative; this only sends
    /// intentions and reads results. Async/await over UnityWebRequest.
    /// </summary>
    public class ApiClient
    {
        private readonly string _authBase;
        private readonly string _worldBase;
        public string Token { get; private set; }

        public ApiClient(string authBase, string worldBase)
        {
            _authBase = authBase.TrimEnd('/');
            _worldBase = worldBase.TrimEnd('/');
        }

        public async Task<TokenResponse> LoginAsync(string username, string password)
        {
            var body = JsonConvert.SerializeObject(new LoginRequest { username = username, password = password });
            var resp = await PostJsonAsync(_authBase + "/api/auth/login", body, auth: false);
            var token = JsonConvert.DeserializeObject<TokenResponse>(resp);
            Token = token.accessToken;
            return token;
        }

        public async Task<TokenResponse> RegisterAsync(string username, string password)
        {
            var body = JsonConvert.SerializeObject(new LoginRequest { username = username, password = password });
            var resp = await PostJsonAsync(_authBase + "/api/auth/register", body, auth: false);
            var token = JsonConvert.DeserializeObject<TokenResponse>(resp);
            Token = token.accessToken;
            return token;
        }

        /// <summary>Register a fresh account, or log in if the username is taken.</summary>
        public async Task<TokenResponse> RegisterOrLoginAsync(string username, string password)
        {
            try { return await RegisterAsync(username, password); }
            catch (ApiException) { return await LoginAsync(username, password); }
        }

        public async Task<List<CharacterDto>> ListCharactersAsync()
        {
            var resp = await GetAsync(_worldBase + "/api/characters");
            return JsonConvert.DeserializeObject<List<CharacterDto>>(resp);
        }

        public async Task<CharacterDto> CreateCharacterAsync(string name)
        {
            var body = JsonConvert.SerializeObject(new CreateCharacterRequest { name = name });
            var resp = await PostJsonAsync(_worldBase + "/api/characters", body, auth: true);
            return JsonConvert.DeserializeObject<CharacterDto>(resp);
        }

        // --- helpers ---

        private async Task<string> GetAsync(string url)
        {
            using var req = UnityWebRequest.Get(url);
            ApplyAuth(req);
            await req.SendWebRequest();
            EnsureOk(req, url);
            return req.downloadHandler.text;
        }

        private async Task<string> PostJsonAsync(string url, string json, bool auth)
        {
            using var req = new UnityWebRequest(url, "POST")
            {
                uploadHandler = new UploadHandlerRaw(Encoding.UTF8.GetBytes(json)),
                downloadHandler = new DownloadHandlerBuffer()
            };
            req.SetRequestHeader("Content-Type", "application/json");
            if (auth) ApplyAuth(req);
            await req.SendWebRequest();
            EnsureOk(req, url);
            return req.downloadHandler.text;
        }

        private void ApplyAuth(UnityWebRequest req)
        {
            if (!string.IsNullOrEmpty(Token)) req.SetRequestHeader("Authorization", "Bearer " + Token);
        }

        private static void EnsureOk(UnityWebRequest req, string url)
        {
            if (req.result != UnityWebRequest.Result.Success)
                throw new ApiException($"{req.responseCode} {req.error} ({url}) :: {req.downloadHandler?.text}");
        }
    }

    public class ApiException : System.Exception
    {
        public ApiException(string message) : base(message) { }
    }

    /// <summary>Lets us `await someUnityWebRequest.SendWebRequest();`.</summary>
    public static class UnityWebRequestAwaiterExtensions
    {
        public static TaskAwaiter<UnityWebRequest> GetAwaiter(this UnityWebRequestAsyncOperation op)
        {
            var tcs = new TaskCompletionSource<UnityWebRequest>();
            if (op.isDone) tcs.SetResult(op.webRequest);
            else op.completed += _ => tcs.SetResult(op.webRequest);
            return tcs.Task.GetAwaiter();
        }
    }
}
