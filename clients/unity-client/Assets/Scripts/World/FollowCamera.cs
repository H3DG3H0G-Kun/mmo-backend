using UnityEngine;

namespace Watcher.World
{
    /// <summary>
    /// A smooth third-person follow camera. WorldManager points <see cref="Target"/> at the local
    /// Watcher on spawn. Tweak Offset/Smooth in the inspector to taste.
    /// </summary>
    public class FollowCamera : MonoBehaviour
    {
        public Transform Target;
        public Vector3 Offset = new(0f, 12f, -9f);
        public float Smooth = 6f;

        private void LateUpdate()
        {
            if (Target == null) return;
            var desired = Target.position + Offset;
            transform.position = Vector3.Lerp(transform.position, desired, Time.deltaTime * Smooth);
            transform.LookAt(Target.position + Vector3.up * 0.5f);
        }
    }
}
