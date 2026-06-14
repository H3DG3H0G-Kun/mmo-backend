using System;
using System.Collections.Generic;

namespace Watcher.Net
{
    // --- REST DTOs (subset we use) ---

    [Serializable]
    public class LoginRequest
    {
        public string username;
        public string password;
    }

    [Serializable]
    public class AccountDto
    {
        public string id;
        public string username;
        public List<string> roles;
    }

    [Serializable]
    public class TokenResponse
    {
        public string accessToken;
        public string tokenType;
        public long expiresInSeconds;
        public AccountDto account;
    }

    [Serializable]
    public class EraDto
    {
        public int id;
        public string code;
        public string name;
    }

    [Serializable]
    public class CharacterDto
    {
        public string id;
        public string name;
        public EraDto currentEra;
    }

    [Serializable]
    public class CreateCharacterRequest
    {
        public string name;
    }

    // --- WebSocket payloads (the "data" object per event) ---

    [Serializable]
    public class EntityDto
    {
        public string characterId;
        public string name;
        public float x;
        public float y;
        public float z;
    }

    [Serializable]
    public class WorldSnapshotData
    {
        public List<EntityDto> entities;
    }
}
