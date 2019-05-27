package com.blankj.utilcode.util;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineHeightSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.text.style.UpdateAppearance;
import android.widget.TextView;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

/**
 * 使用方式：
 *
 * SpanUtils.with(textView)
 * .append(CharSequence)
 * .subSpan(0, 1)
 * .setFontSize((int) getResources().getDimension(R.dimen.text_12_size))
 * .setForegroundColor(getResources().getColor(R.color.theme_color_b))
 * .subSpan(1,"/")
 * .setFontSize((int) getResources().getDimension(R.dimen.text_18_size))
 * .setForegroundColor(getResources().getColor(R.color.theme_color_b))
 * .build();
 *
 * @author: zbb 33775
 * @date: 2019/5/22 15:27
 * @desc:
 */
public class SpanUtils {

    public static final int ALIGN_BOTTOM = 0;
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_CENTER = 2;
    public static final int ALIGN_TOP = 3;

    @IntDef({ALIGN_BOTTOM, ALIGN_BASELINE, ALIGN_CENTER, ALIGN_TOP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Align {

    }

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private TextView mTextView;
    private int mFlag;
    private int mSpanStart;
    private int mSpanEnd;

    private SpannableStringBuilder mBuilder;

    public static SpanUtils with(final TextView textView) {
        return new SpanUtils(textView);
    }


    private SpanUtils(TextView textView) {
        this();
        mTextView = textView;
    }

    public SpanUtils() {
        mBuilder = new SpannableStringBuilder();
    }

    /**
     * Set the span of flag.
     *
     * @param flag The flag.
     *             <ul>
     *             <li>{@link Spanned#SPAN_INCLUSIVE_EXCLUSIVE}</li>
     *             <li>{@link Spanned#SPAN_INCLUSIVE_INCLUSIVE}</li>
     *             <li>{@link Spanned#SPAN_EXCLUSIVE_EXCLUSIVE}</li>
     *             <li>{@link Spanned#SPAN_EXCLUSIVE_INCLUSIVE}</li>
     *             </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setFlag(final int flag) {
        this.mFlag = flag;
        return this;
    }

    /**
     * Set the span of foreground's color.
     *
     * @param color The color of foreground
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setForegroundColor(@ColorInt final int color) {
        mBuilder.setSpan(new ForegroundColorSpan(color), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of background's color.
     *
     * @param color The color of background
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setBackgroundColor(@ColorInt final int color) {
        mBuilder.setSpan(new BackgroundColorSpan(color), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of line height.
     *
     * @param lineHeight The line height, in pixel.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setLineHeight(@IntRange(from = 0) final int lineHeight) {
        return setLineHeight(lineHeight, ALIGN_CENTER);
    }

    /**
     * Set the span of line height.
     *
     * @param lineHeight The line height, in pixel.
     * @param align      The alignment.
     *                   <ul>
     *                   <li>{@link Align#ALIGN_TOP   }</li>
     *                   <li>{@link Align#ALIGN_CENTER}</li>
     *                   <li>{@link Align#ALIGN_BOTTOM}</li>
     *                   </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setLineHeight(@IntRange(from = 0) final int lineHeight, @Align final int align) {
        mBuilder.setSpan(new CustomLineHeightSpan(lineHeight, align), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of quote's color.
     *
     * @param color The color of quote
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setQuoteColor(@ColorInt final int color) {
        return setQuoteColor(color, 2, 2);
    }

    /**
     * Set the span of quote's color.
     *
     * @param color       The color of quote.
     * @param stripeWidth The width of stripe, in pixel.
     * @param gapWidth    The width of gap, in pixel.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setQuoteColor(@ColorInt final int color, @IntRange(from = 1) final int stripeWidth,
            @IntRange(from = 0) final int gapWidth) {
        mBuilder.setSpan(new CustomQuoteSpan(color, stripeWidth, gapWidth), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of leading margin.
     *
     * @param first The indent for the first line of the paragraph.
     * @param rest  The indent for the remaining lines of the paragraph.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setLeadingMargin(@IntRange(from = 0) final int first, @IntRange(from = 0) final int rest) {
        mBuilder.setSpan(new LeadingMarginSpan.Standard(first, rest), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of bullet.
     *
     * @param gapWidth The width of gap, in pixel.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setBullet(@IntRange(from = 0) final int gapWidth) {
        return setBullet(0, 3, gapWidth);
    }

    /**
     * Set the span of bullet.
     *
     * @param color    The color of bullet.
     * @param radius   The radius of bullet, in pixel.
     * @param gapWidth The width of gap, in pixel.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setBullet(@ColorInt final int color,
            @IntRange(from = 0) final int radius,
            @IntRange(from = 0) final int gapWidth) {
        mBuilder.setSpan(new CustomBulletSpan(color, radius, gapWidth), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of font's size.
     *
     * @param size The size of font.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setFontSize(@IntRange(from = 0) final int size) {
        return setFontSize(size, false);
    }

    /**
     * Set the span of size of font.
     *
     * @param size The size of font.
     * @param isSp True to use sp, false to use pixel.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setFontSize(@IntRange(from = 0) final int size, final boolean isSp) {
        mBuilder.setSpan(new AbsoluteSizeSpan(size, isSp), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of proportion of font.
     *
     * @param proportion The proportion of font.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setFontProportion(final float proportion) {
        mBuilder.setSpan(new RelativeSizeSpan(proportion), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of transverse proportion of font.
     *
     * @param proportion The transverse proportion of font.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setFontXProportion(final float proportion) {
        mBuilder.setSpan(new ScaleXSpan(proportion), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of strikethrough.
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setStrikethrough() {
        mBuilder.setSpan(new StrikethroughSpan(), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of underline.
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setUnderline() {
        mBuilder.setSpan(new UnderlineSpan(), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of superscript.
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setSuperscript() {
        mBuilder.setSpan(new SuperscriptSpan(), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of subscript.
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setSubscript() {
        mBuilder.setSpan(new SubscriptSpan(), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of bold.
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setBold() {
        mBuilder.setSpan(new StyleSpan(Typeface.BOLD), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of italic.
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setItalic() {
        mBuilder.setSpan(new StyleSpan(Typeface.ITALIC), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of bold italic.
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setBoldItalic() {
        mBuilder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of font family.
     *
     * @param fontFamily The font family.
     *                   <ul>
     *                   <li>monospace</li>
     *                   <li>serif</li>
     *                   <li>sans-serif</li>
     *                   </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setFontFamily(@NonNull final String fontFamily) {
        mBuilder.setSpan(new TypefaceSpan(fontFamily), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of typeface.
     *
     * @param typeface The typeface.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setTypeface(@NonNull final Typeface typeface) {
        mBuilder.setSpan(new CustomTypefaceSpan(typeface), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of horizontal alignment.
     *
     * @param alignment The alignment.
     *                  <ul>
     *                  <li>{@link Alignment#ALIGN_NORMAL  }</li>
     *                  <li>{@link Alignment#ALIGN_OPPOSITE}</li>
     *                  <li>{@link Alignment#ALIGN_CENTER  }</li>
     *                  </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setHorizontalAlign(@NonNull final Alignment alignment) {
        mBuilder.setSpan(new AlignmentSpan.Standard(alignment), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of vertical alignment.
     *
     * @param align The alignment.
     *              <ul>
     *              <li>{@link Align#ALIGN_TOP     }</li>
     *              <li>{@link Align#ALIGN_CENTER  }</li>
     *              <li>{@link Align#ALIGN_BASELINE}</li>
     *              <li>{@link Align#ALIGN_BOTTOM  }</li>
     *              </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setVerticalAlign(@Align final int align) {
        mBuilder.setSpan(new VerticalAlignSpan(align), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of click.
     * <p>Must set {@code view.setMovementMethod(LinkMovementMethod.getInstance())}</p>
     *
     * @param clickSpan The span of click.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setClickSpan(@NonNull final ClickableSpan clickSpan) {
        if (mTextView != null && mTextView.getMovementMethod() == null) {
            mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        mBuilder.setSpan(clickSpan, mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of url.
     * <p>Must set {@code view.setMovementMethod(LinkMovementMethod.getInstance())}</p>
     *
     * @param url The url.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setUrl(@NonNull final String url) {
        if (mTextView != null && mTextView.getMovementMethod() == null) {
            mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        mBuilder.setSpan(new URLSpan(url), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of blur.
     *
     * @param radius The radius of blur.
     * @param style  The style.
     *               <ul>
     *               <li>{@link Blur#NORMAL}</li>
     *               <li>{@link Blur#SOLID}</li>
     *               <li>{@link Blur#OUTER}</li>
     *               <li>{@link Blur#INNER}</li>
     *               </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setBlur(@FloatRange(from = 0, fromInclusive = false) final float radius, final Blur style) {
        mBuilder.setSpan(new MaskFilterSpan(new BlurMaskFilter(radius, style)), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of shader.
     *
     * @param shader The shader.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setShader(@NonNull final Shader shader) {
        mBuilder.setSpan(new ShaderSpan(shader), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Set the span of shadow.
     *
     * @param radius      The radius of shadow.
     * @param dx          X-axis offset, in pixel.
     * @param dy          Y-axis offset, in pixel.
     * @param shadowColor The color of shadow.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils setShadow(@FloatRange(from = 0, fromInclusive = false) final float radius,
            final float dx,
            final float dy,
            final int shadowColor) {
        mBuilder.setSpan(new ShadowSpan(radius, dx, dy, shadowColor), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    public SpanUtils subSpan(int start, int end) {
        mSpanStart = start;
        mSpanEnd = end;
        return this;
    }

    public SpanUtils subSpan(String charStart, String charEnd) {
        String s = mBuilder.toString();
        mSpanStart = s.indexOf(charStart);
        mSpanEnd = s.indexOf(charEnd);
        return this;
    }

    public SpanUtils subSpan(int start, String charEnd) {
        String s = mBuilder.toString();
        mSpanStart = start;
        mSpanEnd = s.indexOf(charEnd);
        return this;
    }

    public SpanUtils subSpan(String charStart, int end) {
        String s = mBuilder.toString();
        mSpanStart = s.indexOf(charStart);
        mSpanEnd = end;
        return this;
    }

    /**
     * Append the text text.
     *
     * @param text The text.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils append(@NonNull final CharSequence text) {
        mSpanStart = mBuilder.length();
        mBuilder.append(text);
        mSpanEnd = mBuilder.length();
        return this;
    }

    /**
     * Append one line.
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendLine() {
        append(LINE_SEPARATOR);
        return this;
    }

    /**
     * Append text and one line.
     *
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendLine(@NonNull final CharSequence text) {
        append(text + LINE_SEPARATOR);
        return this;
    }

    /**
     * Append one image.
     *
     * @param bitmap The bitmap of image.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@NonNull final Bitmap bitmap) {
        return appendImage(bitmap, ALIGN_BOTTOM);
    }

    /**
     * Append one image.
     *
     * @param bitmap The bitmap.
     * @param align  The alignment.
     *               <ul>
     *               <li>{@link Align#ALIGN_TOP     }</li>
     *               <li>{@link Align#ALIGN_CENTER  }</li>
     *               <li>{@link Align#ALIGN_BASELINE}</li>
     *               <li>{@link Align#ALIGN_BOTTOM  }</li>
     *               </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@NonNull final Bitmap bitmap, @Align final int align) {
        calculateImageSpan();
        mBuilder.setSpan(new CustomImageSpan(bitmap, align), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Append one image.
     *
     * @param drawable The drawable of image.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@NonNull final Drawable drawable) {
        return appendImage(drawable, ALIGN_BOTTOM);
    }

    /**
     * Append one image.
     *
     * @param drawable The drawable of image.
     * @param align    The alignment.
     *                 <ul>
     *                 <li>{@link Align#ALIGN_TOP     }</li>
     *                 <li>{@link Align#ALIGN_CENTER  }</li>
     *                 <li>{@link Align#ALIGN_BASELINE}</li>
     *                 <li>{@link Align#ALIGN_BOTTOM  }</li>
     *                 </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@NonNull final Drawable drawable, @Align final int align) {
        calculateImageSpan();
        mBuilder.setSpan(new CustomImageSpan(drawable, align), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Append one image.
     *
     * @param uri The uri of image.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@NonNull final Uri uri) {
        return appendImage(uri, ALIGN_BOTTOM);
    }

    /**
     * Append one image.
     *
     * @param uri   The uri of image.
     * @param align The alignment.
     *              <ul>
     *              <li>{@link Align#ALIGN_TOP     }</li>
     *              <li>{@link Align#ALIGN_CENTER  }</li>
     *              <li>{@link Align#ALIGN_BASELINE}</li>
     *              <li>{@link Align#ALIGN_BOTTOM  }</li>
     *              </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@NonNull final Uri uri, @Align final int align) {
        calculateImageSpan();
        mBuilder.setSpan(new CustomImageSpan(uri, align), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    /**
     * Append one image.
     *
     * @param resourceId The resource id of image.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@DrawableRes final int resourceId) {
        return appendImage(resourceId, ALIGN_BOTTOM);
    }

    /**
     * Append one image.
     *
     * @param resourceId The resource id of image.
     * @param align      The alignment.
     *                   <ul>
     *                   <li>{@link Align#ALIGN_TOP     }</li>
     *                   <li>{@link Align#ALIGN_CENTER  }</li>
     *                   <li>{@link Align#ALIGN_BASELINE}</li>
     *                   <li>{@link Align#ALIGN_BOTTOM  }</li>
     *                   </ul>
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendImage(@DrawableRes final int resourceId, @Align final int align) {
        calculateImageSpan();
        mBuilder.setSpan(new CustomImageSpan(resourceId, align), mSpanStart, mSpanEnd, mFlag);
        return this;
    }

    private void calculateImageSpan() {
        mSpanStart = mBuilder.length();
        if (mSpanStart == 0) {
            mBuilder.append(Character.toString((char) 2));
            mSpanStart = 1;
        }
        mBuilder.append("<img>");
        mSpanEnd = mSpanStart + 5;
    }

    /**
     * Append space.
     *
     * @param size The size of space.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendSpace(@IntRange(from = 0) final int size) {
        return appendSpace(size, Color.TRANSPARENT);
    }

    /**
     * Append space.
     *
     * @param size  The size of space.
     * @param color The color of space.
     * @return the single {@link SpanUtils} instance
     */
    public SpanUtils appendSpace(@IntRange(from = 0) final int size, @ColorInt final int color) {
        int start = mBuilder.length();
        mBuilder.append("< >");
        int end = start + 3;
        mBuilder.setSpan(new SpaceSpan(size, color), start, end, mFlag);
        return this;
    }

    /**
     * Create the span string.
     *
     * @return the span string
     */
    public SpannableStringBuilder build() {
        if (mTextView != null) {
            mTextView.setText(mBuilder);
        }
        return mBuilder;
    }

    static class VerticalAlignSpan extends ReplacementSpan {

        static final int ALIGN_CENTER = 2;
        static final int ALIGN_TOP = 3;

        final int mVerticalAlignment;

        VerticalAlignSpan(int verticalAlignment) {
            mVerticalAlignment = verticalAlignment;
        }

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end,
                @Nullable Paint.FontMetricsInt fm) {
            text = text.subSequence(start, end);
            return (int) paint.measureText(text.toString());
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
                int bottom, @NonNull Paint paint) {
            text = text.subSequence(start, end);
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();

            canvas.drawText(text.toString(), x, y - ((y + fm.descent + y + fm.ascent) / 2 - (bottom + top) / 2), paint);
        }

    }

    static class CustomLineHeightSpan implements LineHeightSpan {

        private final int height;

        static final int ALIGN_CENTER = 2;
        static final int ALIGN_TOP = 3;

        final int mVerticalAlignment;
        static Paint.FontMetricsInt sfm;

        CustomLineHeightSpan(int height, int verticalAlignment) {
            this.height = height;
            mVerticalAlignment = verticalAlignment;
        }

        @Override
        public void chooseHeight(final CharSequence text, final int start, final int end,
                final int spanstartv, final int v, final Paint.FontMetricsInt fm) {
            if (sfm == null) {
                sfm = new Paint.FontMetricsInt();
                sfm.top = fm.top;
                sfm.ascent = fm.ascent;
                sfm.descent = fm.descent;
                sfm.bottom = fm.bottom;
                sfm.leading = fm.leading;
            } else {
                fm.top = sfm.top;
                fm.ascent = sfm.ascent;
                fm.descent = sfm.descent;
                fm.bottom = sfm.bottom;
                fm.leading = sfm.leading;
            }
            int need = height - (v + fm.descent - fm.ascent - spanstartv);
            if (need > 0) {
                if (mVerticalAlignment == ALIGN_TOP) {
                    fm.descent += need;
                } else if (mVerticalAlignment == ALIGN_CENTER) {
                    fm.descent += need / 2;
                    fm.ascent -= need / 2;
                } else {
                    fm.ascent -= need;
                }
            }
            need = height - (v + fm.bottom - fm.top - spanstartv);
            if (need > 0) {
                if (mVerticalAlignment == ALIGN_TOP) {
                    fm.bottom += need;
                } else if (mVerticalAlignment == ALIGN_CENTER) {
                    fm.bottom += need / 2;
                    fm.top -= need / 2;
                } else {
                    fm.top -= need;
                }
            }
            if (end == ((Spanned) text).getSpanEnd(this)) {
                sfm = null;
            }
        }

    }

    static class SpaceSpan extends ReplacementSpan {

        private final int width;
        private final int color;

        private SpaceSpan(final int width) {
            this(width, Color.TRANSPARENT);
        }

        private SpaceSpan(final int width, final int color) {
            super();
            this.width = width;
            this.color = color;
        }

        @Override
        public int getSize(@NonNull final Paint paint, final CharSequence text,
                @IntRange(from = 0) final int start,
                @IntRange(from = 0) final int end,
                @Nullable final Paint.FontMetricsInt fm) {
            return width;
        }

        @Override
        public void draw(@NonNull final Canvas canvas, final CharSequence text,
                @IntRange(from = 0) final int start,
                @IntRange(from = 0) final int end,
                final float x, final int top, final int y, final int bottom,
                @NonNull final Paint paint) {
            Paint.Style style = paint.getStyle();
            int color = paint.getColor();

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(this.color);

            canvas.drawRect(x, top, x + width, bottom, paint);

            paint.setStyle(style);
            paint.setColor(color);
        }

    }

    static class CustomQuoteSpan implements LeadingMarginSpan {

        private final int color;
        private final int stripeWidth;
        private final int gapWidth;

        private CustomQuoteSpan(final int color, final int stripeWidth, final int gapWidth) {
            super();
            this.color = color;
            this.stripeWidth = stripeWidth;
            this.gapWidth = gapWidth;
        }

        public int getLeadingMargin(final boolean first) {
            return stripeWidth + gapWidth;
        }

        public void drawLeadingMargin(final Canvas c, final Paint p, final int x, final int dir,
                final int top, final int baseline, final int bottom,
                final CharSequence text, final int start, final int end,
                final boolean first, final Layout layout) {
            Paint.Style style = p.getStyle();
            int color = p.getColor();

            p.setStyle(Paint.Style.FILL);
            p.setColor(this.color);

            c.drawRect(x, top, x + dir * stripeWidth, bottom, p);

            p.setStyle(style);
            p.setColor(color);
        }

    }

    static class CustomBulletSpan implements LeadingMarginSpan {

        private final int color;
        private final int radius;
        private final int gapWidth;

        private Path sBulletPath = null;

        private CustomBulletSpan(final int color, final int radius, final int gapWidth) {
            this.color = color;
            this.radius = radius;
            this.gapWidth = gapWidth;
        }

        public int getLeadingMargin(final boolean first) {
            return 2 * radius + gapWidth;
        }

        public void drawLeadingMargin(final Canvas c, final Paint p, final int x, final int dir,
                final int top, final int baseline, final int bottom,
                final CharSequence text, final int start, final int end,
                final boolean first, final Layout l) {
            if (((Spanned) text).getSpanStart(this) == start) {
                Paint.Style style = p.getStyle();
                int oldColor = 0;
                oldColor = p.getColor();
                p.setColor(color);
                p.setStyle(Paint.Style.FILL);
                if (c.isHardwareAccelerated()) {
                    if (sBulletPath == null) {
                        sBulletPath = new Path();
                        // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                        sBulletPath.addCircle(0.0f, 0.0f, radius, Path.Direction.CW);
                    }
                    c.save();
                    c.translate(x + dir * radius, (top + bottom) / 2.0f);
                    c.drawPath(sBulletPath, p);
                    c.restore();
                } else {
                    c.drawCircle(x + dir * radius, (top + bottom) / 2.0f, radius, p);
                }
                p.setColor(oldColor);
                p.setStyle(style);
            }
        }

    }

    @SuppressLint("ParcelCreator")
    static class CustomTypefaceSpan extends TypefaceSpan {

        private final Typeface newType;

        private CustomTypefaceSpan(final Typeface type) {
            super("");
            newType = type;
        }

        @Override
        public void updateDrawState(final TextPaint textPaint) {
            apply(textPaint, newType);
        }

        @Override
        public void updateMeasureState(final TextPaint paint) {
            apply(paint, newType);
        }

        private void apply(final Paint paint, final Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.getShader();

            paint.setTypeface(tf);
        }

    }

    static class CustomImageSpan extends CustomDynamicDrawableSpan {

        private Drawable mDrawable;
        private Uri mContentUri;
        private int mResourceId;

        private CustomImageSpan(final Bitmap b, final int verticalAlignment) {
            super(verticalAlignment);
            mDrawable = new BitmapDrawable(Utils.getApp().getResources(), b);
            mDrawable.setBounds(
                    0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight()
            );
        }

        private CustomImageSpan(final Drawable d, final int verticalAlignment) {
            super(verticalAlignment);
            mDrawable = d;
            mDrawable.setBounds(
                    0, 0, mDrawable.getIntrinsicWidth(), mDrawable.getIntrinsicHeight()
            );
        }

        private CustomImageSpan(final Uri uri, final int verticalAlignment) {
            super(verticalAlignment);
            mContentUri = uri;
        }

        private CustomImageSpan(@DrawableRes final int resourceId, final int verticalAlignment) {
            super(verticalAlignment);
            mResourceId = resourceId;
        }

        @Override
        public Drawable getDrawable() {
            Drawable drawable = null;
            if (mDrawable != null) {
                drawable = mDrawable;
            } else if (mContentUri != null) {
                Bitmap bitmap;
                try {
                    InputStream is =
                            Utils.getApp().getContentResolver().openInputStream(mContentUri);
                    bitmap = BitmapFactory.decodeStream(is);
                    drawable = new BitmapDrawable(Utils.getApp().getResources(), bitmap);
                    drawable.setBounds(
                            0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()
                    );
                    if (is != null) {
                        is.close();
                    }
                } catch (Exception e) {
                    LogUtils.e("Failed to loaded content " + mContentUri, e);
                }
            } else {
                try {
                    drawable = ContextCompat.getDrawable(Utils.getApp(), mResourceId);
                    drawable.setBounds(
                            0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()
                    );
                } catch (Exception e) {
                    LogUtils.e("Unable to find resource: " + mResourceId);
                }
            }
            return drawable;
        }

    }

    static abstract class CustomDynamicDrawableSpan extends ReplacementSpan {

        static final int ALIGN_BOTTOM = 0;

        static final int ALIGN_BASELINE = 1;

        static final int ALIGN_CENTER = 2;

        static final int ALIGN_TOP = 3;

        final int mVerticalAlignment;

        private CustomDynamicDrawableSpan() {
            mVerticalAlignment = ALIGN_BOTTOM;
        }

        private CustomDynamicDrawableSpan(final int verticalAlignment) {
            mVerticalAlignment = verticalAlignment;
        }

        public abstract Drawable getDrawable();

        @Override
        public int getSize(@NonNull final Paint paint, final CharSequence text,
                final int start, final int end, final Paint.FontMetricsInt fm) {
            Drawable d = getCachedDrawable();
            Rect rect = d.getBounds();
            if (fm != null) {
                int lineHeight = fm.bottom - fm.top;
                if (lineHeight < rect.height()) {
                    if (mVerticalAlignment == ALIGN_TOP) {
                        fm.bottom = rect.height() + fm.top;
                    } else if (mVerticalAlignment == ALIGN_CENTER) {
                        fm.top = -rect.height() / 2 - lineHeight / 4;
                        fm.bottom = rect.height() / 2 - lineHeight / 4;
                    } else {
                        fm.top = -rect.height() + fm.bottom;
                    }
                    fm.ascent = fm.top;
                    fm.descent = fm.bottom;
                }
            }
            return rect.right;
        }

        @Override
        public void draw(@NonNull final Canvas canvas, final CharSequence text,
                final int start, final int end, final float x,
                final int top, final int y, final int bottom, @NonNull final Paint paint) {
            Drawable d = getCachedDrawable();
            Rect rect = d.getBounds();
            canvas.save();
            float transY;
            int lineHeight = bottom - top;
            if (rect.height() < lineHeight) {
                if (mVerticalAlignment == ALIGN_TOP) {
                    transY = top;
                } else if (mVerticalAlignment == ALIGN_CENTER) {
                    transY = (bottom + top - rect.height()) / 2;
                } else if (mVerticalAlignment == ALIGN_BASELINE) {
                    transY = y - rect.height();
                } else {
                    transY = bottom - rect.height();
                }
                canvas.translate(x, transY);
            } else {
                canvas.translate(x, top);
            }
            d.draw(canvas);
            canvas.restore();
        }

        private Drawable getCachedDrawable() {
            WeakReference<Drawable> wr = mDrawableRef;
            Drawable d = null;
            if (wr != null) {
                d = wr.get();
            }
            if (d == null) {
                d = getDrawable();
                mDrawableRef = new WeakReference<>(d);
            }
            return d;
        }

        private WeakReference<Drawable> mDrawableRef;

    }

    static class ShaderSpan extends CharacterStyle implements UpdateAppearance {

        private Shader mShader;

        private ShaderSpan(final Shader shader) {
            this.mShader = shader;
        }

        @Override
        public void updateDrawState(final TextPaint tp) {
            tp.setShader(mShader);
        }

    }

    static class ShadowSpan extends CharacterStyle implements UpdateAppearance {

        private float radius;
        private float dx, dy;
        private int shadowColor;

        private ShadowSpan(final float radius,
                final float dx,
                final float dy,
                final int shadowColor) {
            this.radius = radius;
            this.dx = dx;
            this.dy = dy;
            this.shadowColor = shadowColor;
        }

        @Override
        public void updateDrawState(final TextPaint tp) {
            tp.setShadowLayer(radius, dx, dy, shadowColor);
        }

    }

}
