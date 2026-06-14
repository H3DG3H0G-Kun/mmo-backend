using UnityEngine;

namespace Watcher.World
{
    /// <summary>A remote Watcher rendered as a capsule that smoothly chases its server position.</summary>
    public class RemoteWatcher : MonoBehaviour
    {
        public Vector3 Target;
        public float LerpSpeed = 8f;

        public void SetTarget(Vector3 target) => Target = target;

        private void Update()
        {
            transform.position = Vector3.Lerp(transform.position, Target, Time.deltaTime * LerpSpeed);
        }
    }
}
