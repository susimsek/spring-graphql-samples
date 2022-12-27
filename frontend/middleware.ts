import {NextRequest, NextResponse} from "next/server";

export function middleware(request: NextRequest){
    const cookie = request.cookies.get("token")?.value
    console.log('cookie -> ' + cookie)
    if (cookie === undefined) {
        const url = request.nextUrl.clone()
        url.pathname = '/login'
        url.searchParams.set("redirectUrl", request.nextUrl.pathname)
        return NextResponse.redirect(url)
    }
    return NextResponse.next()
}

export const config = {
    matcher: [
        /*
         * Match all request paths except for the ones starting with:
         * - api (API routes)
         * - _next/static (static files)
         * - favicon.ico (favicon file)
         */
        '/((?!api|_next/static|favicon.ico|login).*)',
    ],
}