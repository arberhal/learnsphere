import { sveltekit } from "@sveltejs/kit/vite";
import { defineConfig } from "vite";
import tailwindcss from "@tailwindcss/vite";

export default defineConfig({
  plugins: [sveltekit(), tailwindcss()],
  preview: {
    host: "0.0.0.0",
    port: 3000,
    allowedHosts: ["learnsphere.azurewebsites.net"],
  },
});
