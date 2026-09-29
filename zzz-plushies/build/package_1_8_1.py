from pathlib import Path
import hashlib
import json
import shutil
import zipfile

root = Path(__file__).resolve().parents[1]
release = root / "versions" / "1.8.1"
release.mkdir(parents=True, exist_ok=True)
jar_name = "zzz-plushies-forge-1.20.1-1.8.1.jar"
shutil.copy2(root / "build" / "libs" / jar_name, release / jar_name)
shutil.copy2(release / jar_name, root / "output" / jar_name)

top_level = [
    "build.gradle", "settings.gradle", "gradle.properties", "gradlew", "gradlew.bat",
    "generate_assets.py", "README.md", "SKIN_SOURCES.md", "VOICE_SOURCES.md",
    "SOUND_SOURCES.md", "A_RANK_SOURCES.md", "fetch_agent_voices.py",
    "resolve_agent_voices.py", "download_agent_voices.py", "voice_page_audit.json",
    "voice_file_audit.json",
]
files = [file for dirname in ("src", "gradle", "verification")
         for file in (root / dirname).rglob("*") if file.is_file()]
files += [root / name for name in top_level]
zip_path = release / "source-1.8.1.zip"
with zipfile.ZipFile(zip_path, "w", zipfile.ZIP_DEFLATED, compresslevel=9) as archive:
    for file in files:
        archive.write(file, file.relative_to(root).as_posix())
with zipfile.ZipFile(zip_path) as archive:
    assert archive.testzip() is None
    assert len(archive.namelist()) == len(files)

manifest = {
    file.name: {
        "bytes": file.stat().st_size,
        "sha256": hashlib.sha256(file.read_bytes()).hexdigest(),
    }
    for file in (release / jar_name, zip_path)
}
(release / "manifest.json").write_text(json.dumps(manifest, indent=2) + "\n", encoding="utf-8")
print(json.dumps(manifest, indent=2))
