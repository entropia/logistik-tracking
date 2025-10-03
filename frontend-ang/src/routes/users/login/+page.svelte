<script lang="ts">
	import { PUBLIC_API_URL } from '$env/static/public';
    let target = PUBLIC_API_URL+"/api/login";
</script>

<form action="{target}" method="POST">
    <input name="username" placeholder="username" required>
    <input name="password" placeholder="password" required>
    <input type="submit" value="go">
</form>