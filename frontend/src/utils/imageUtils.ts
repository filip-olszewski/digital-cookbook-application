export const getOptimizedImageUrl = (
  url: string | undefined,
  width: number = 600
) => {
  if (!url) return '';
  if (url.includes('images.unsplash.com')) {
    const separator = url.includes('?') ? '&' : '?';
    return `${url}${separator}w=${width}&q=80&auto=format`;
  }
  return url;
};
